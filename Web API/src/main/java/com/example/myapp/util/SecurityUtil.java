package com.example.myapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.myapp.constant.RolesConstants;

public final class SecurityUtil {

	public static final String CLAIMS_NAMESPACE = "https://www.jhipster.tech/";

	private SecurityUtil() {
	}

	public static Optional<String> getCurrentUserLogin() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
	}

	private static String extractPrincipal(Authentication authentication) {
		if (authentication == null) {
			return null;
		} else if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
			return springSecurityUser.getUsername();
		} else if (authentication.getPrincipal() instanceof String) {
			return (String) authentication.getPrincipal();
		}
		return null;
	}

	public static Optional<String> getCurrentUserJWT() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		return Optional.ofNullable(securityContext.getAuthentication())
				.filter(authentication -> authentication.getCredentials() instanceof String)
				.map(authentication -> (String) authentication.getCredentials());
	}

	public static boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && getAuthorities(authentication).noneMatch(RolesConstants.ANONYMOUS::equals);
	}

	public static boolean hasCurrentUserThisAuthority(String role) {
		return hasCurrentUserAnyOfAuthorities(role);
	}

	public static boolean hasCurrentUserAnyOfAuthorities(String... roles) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (authentication != null
				&& getAuthorities(authentication).anyMatch(authority -> Arrays.asList(roles).contains(authority)));
	}

	public static boolean hasCurrentUserNoneOfAuthorities(String... roles) {
		return !hasCurrentUserAnyOfAuthorities(roles);
	}

	private static Stream<String> getAuthorities(Authentication authentication) {
		return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
	}

	public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
		return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
	}

	@SuppressWarnings("unchecked")
	private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
		return (Collection<String>) claims.getOrDefault("groups",
				claims.getOrDefault("roles", claims.getOrDefault(CLAIMS_NAMESPACE + "roles", new ArrayList<>())));
	}

	private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
		return roles.stream().filter(role -> role.startsWith("ROLE_")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

}
