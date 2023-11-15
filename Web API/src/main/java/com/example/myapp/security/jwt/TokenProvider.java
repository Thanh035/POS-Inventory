package com.example.myapp.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.example.myapp.service.SecurityMetersService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenProvider {
	private final Key key;

	private final JwtParser jwtParser;

	private final long tokenValidityInMilliseconds;

	private final long tokenValidityInMillisecondsForRememberMe;

	private final SecurityMetersService securityMetersService;

	private static final String ROLES_KEY = "auth";

	private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";

	private static final String BASE64_SECRET = "MWExY2QxZjM1MmRhZDAxMjNmZjExYTg5MmQ1MTk2YTE5NjI0NDYxMTg4ZmI0YjdmNjVhOGFkYmY0NGZiYmVmYTI1ZTM3ZGQ1ODJiZmQ0YzI0NGYwYTU3YjdiNjIzODVhOWY1YTc2NGY1MTQ4NmNjY2IxMjM0MDE1NGM0NDRiYmQ=";

//	@Value("${myapp.security.authentication.jwt.base64-secret}")
	private String jwtKey = "MWM3MjZhMzk4ZmY2NGJhZmQ5N2IzM2YxNjE4YThkN2ZkYmE2NGIxZTlmMzNiYWRmNjQ0MGJiMjRmNThhNTI2MTMwOTRhNTZhZjI0YmJkMjcyNjM3ZmU5ODJmYzdiMzE0OWQ3NmY3NmI2Y2EyZTQzYzY3M2IyNWU4ZTFkM2Q0YmY=";

//	@Value("${myapp.security.authentication.jwt.token-validity-in-seconds}")
	private long TOKEN_VALIDITY_IN_SECONDS = 86400;

//	@Value("${myapp.security.authentication.jwt.token-validity-in-seconds-for-remember-me}")
	private long TOKEN_VALIDITY_IN_SECONDS_FOR_REMEMBER_ME = 2592000;

	public TokenProvider(SecurityMetersService securityMetersService) {
		byte[] keyBytes;
		String secret = jwtKey;

		if (!ObjectUtils.isEmpty(secret)) {
			log.debug("Using a Base64-encoded JWT secret key");
			keyBytes = Decoders.BASE64.decode(secret);
		} else {
			log.warn("Warning: the JWT key used is not Base64-encoded. "
					+ "We recommend using base64-secret key for optimum security.");
			secret = BASE64_SECRET;
			keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		}

		key = Keys.hmacShaKeyFor(keyBytes);
		jwtParser = Jwts.parserBuilder().setSigningKey(key).build();

		this.tokenValidityInMilliseconds = 1000 * TOKEN_VALIDITY_IN_SECONDS;
		this.tokenValidityInMillisecondsForRememberMe = 1000 * TOKEN_VALIDITY_IN_SECONDS_FOR_REMEMBER_ME;
		this.securityMetersService = securityMetersService;
	}

	public String createToken(Authentication authentication, boolean rememberMe) {
		String roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity;
		if (rememberMe) {
			validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
		} else {
			validity = new Date(now + this.tokenValidityInMilliseconds);
		}

		return Jwts.builder().setSubject(authentication.getName()).claim(ROLES_KEY, roles)
				.signWith(key, SignatureAlgorithm.HS512).setExpiration(validity).compact();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = jwtParser.parseClaimsJws(token).getBody();

		Collection<? extends GrantedAuthority> roles = Arrays.stream(claims.get(ROLES_KEY).toString().split(","))
				.filter(auth -> !auth.trim().isEmpty()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		User principal = new User(claims.getSubject(), "", roles);

		return new UsernamePasswordAuthenticationToken(principal, token, roles);
	}

	public boolean validateToken(String authToken) {
		try {
			jwtParser.parseClaimsJws(authToken);
			return true;
		} catch (ExpiredJwtException e) {
			this.securityMetersService.trackTokenExpired();

			log.trace(INVALID_JWT_TOKEN, e);
		} catch (UnsupportedJwtException e) {
			this.securityMetersService.trackTokenUnsupported();

			log.trace(INVALID_JWT_TOKEN, e);
		} catch (MalformedJwtException e) {
			this.securityMetersService.trackTokenMalformed();

			log.trace(INVALID_JWT_TOKEN, e);
		} catch (SignatureException e) {
			this.securityMetersService.trackTokenInvalidSignature();
			log.trace(INVALID_JWT_TOKEN, e);
		} catch (IllegalArgumentException e) {
			log.error("Token validation error {}", e.getMessage());
		}

		return false;
	}

}