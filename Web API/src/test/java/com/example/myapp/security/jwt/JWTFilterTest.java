package com.example.myapp.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.myapp.constant.RolesConstants;
import com.example.myapp.service.SecurityMetersService;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class JWTFilterTest {

	private TokenProvider tokenProvider;

	private JWTFilter jwtFilter;

	@BeforeEach
	public void setup() {
		String base64Secret = "MWExY2QxZjM1MmRhZDAxMjNmZjExYTg5MmQ1MTk2YTE5NjI0NDYxMTg4ZmI0YjdmNjVhOGFkYmY0NGZiYmVmYTI1ZTM3ZGQ1ODJiZmQ0YzI0NGYwYTU3YjdiNjIzODVhOWY1YTc2NGY1MTQ4NmNjY2IxMjM0MDE1NGM0NDRiYmQ=";
		
		SecurityMetersService securityMetersService = new SecurityMetersService(new SimpleMeterRegistry());
		tokenProvider = new TokenProvider(securityMetersService);
		ReflectionTestUtils.setField(tokenProvider, "key", Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret)));
		ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", 60000);
		jwtFilter = new JWTFilter(tokenProvider);
		SecurityContextHolder.getContext().setAuthentication(null);
	}

//	@Test
//	void testJWTFilter() throws Exception {
//		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test-user",
//				"test-password", Collections.singletonList(new SimpleGrantedAuthority(RolesConstants.USER)));
//		String jwt = tokenProvider.createToken(authentication, false);
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
//		request.setRequestURI("/api/test");
//		MockHttpServletResponse response = new MockHttpServletResponse();
//		MockFilterChain filterChain = new MockFilterChain();
//		jwtFilter.doFilter(request, response, filterChain);
//		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//		assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("test-user");
//		assertThat(SecurityContextHolder.getContext().getAuthentication().getCredentials()).hasToString(jwt);
//	}

	@Test
	void testJWTFilterInvalidToken() throws Exception {
		String jwt = "wrong_jwt";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
		request.setRequestURI("/api/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		jwtFilter.doFilter(request, response, filterChain);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void testJWTFilterMissingAuthorization() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/api/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		jwtFilter.doFilter(request, response, filterChain);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void testJWTFilterMissingToken() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Bearer ");
		request.setRequestURI("/api/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		jwtFilter.doFilter(request, response, filterChain);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}

	@Test
	void testJWTFilterWrongScheme() throws Exception {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test-user",
				"test-password", Collections.singletonList(new SimpleGrantedAuthority(RolesConstants.USER)));
		String jwt = tokenProvider.createToken(authentication, false);
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(JWTFilter.AUTHORIZATION_HEADER, "Basic " + jwt);
		request.setRequestURI("/api/test");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();
		jwtFilter.doFilter(request, response, filterChain);
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
	}
}
