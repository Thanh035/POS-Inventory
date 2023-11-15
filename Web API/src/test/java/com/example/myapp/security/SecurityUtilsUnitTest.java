package com.example.myapp.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.myapp.constant.RolesConstants;
import com.example.myapp.util.SecurityUtil;

class SecurityUtilsUnitTest {

	@BeforeEach
	@AfterEach
	void cleanup() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void testGetCurrentUserLogin() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
		SecurityContextHolder.setContext(securityContext);
		Optional<String> login = SecurityUtil.getCurrentUserLogin();
		assertThat(login).contains("admin");
	}

	@Test
	void testGetCurrentUserJWT() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "token"));
		SecurityContextHolder.setContext(securityContext);
		Optional<String> jwt = SecurityUtil.getCurrentUserJWT();
		assertThat(jwt).contains("token");
	}

	@Test
	void testIsAuthenticated() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
		SecurityContextHolder.setContext(securityContext);
		boolean isAuthenticated = SecurityUtil.isAuthenticated();
		assertThat(isAuthenticated).isTrue();
	}

	@Test
	void testAnonymousIsNotAuthenticated() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(RolesConstants.ANONYMOUS));
		securityContext
				.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
		SecurityContextHolder.setContext(securityContext);
		boolean isAuthenticated = SecurityUtil.isAuthenticated();
		assertThat(isAuthenticated).isFalse();
	}

	@Test
	void testHasCurrentUserThisAuthority() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(RolesConstants.USER));
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
		SecurityContextHolder.setContext(securityContext);

		assertThat(SecurityUtil.hasCurrentUserThisAuthority(RolesConstants.USER)).isTrue();
		assertThat(SecurityUtil.hasCurrentUserThisAuthority(RolesConstants.ADMIN)).isFalse();
	}

	@Test
	void testHasCurrentUserAnyOfAuthorities() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(RolesConstants.USER));
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
		SecurityContextHolder.setContext(securityContext);

		assertThat(SecurityUtil.hasCurrentUserAnyOfAuthorities(RolesConstants.USER, RolesConstants.ADMIN)).isTrue();
		assertThat(SecurityUtil.hasCurrentUserAnyOfAuthorities(RolesConstants.ANONYMOUS, RolesConstants.ADMIN))
				.isFalse();
	}

	@Test
	void testHasCurrentUserNoneOfAuthorities() {
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(RolesConstants.USER));
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
		SecurityContextHolder.setContext(securityContext);

		assertThat(SecurityUtil.hasCurrentUserNoneOfAuthorities(RolesConstants.USER, RolesConstants.ADMIN)).isFalse();
		assertThat(SecurityUtil.hasCurrentUserNoneOfAuthorities(RolesConstants.ANONYMOUS, RolesConstants.ADMIN))
				.isTrue();
	}
}
