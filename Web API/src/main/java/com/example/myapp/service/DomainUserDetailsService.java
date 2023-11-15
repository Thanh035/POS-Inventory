package com.example.myapp.service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.exception.UserNotActivatedException;
import com.example.myapp.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component("userDetailsService")
@AllArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
		log.debug("Authenticating {}", login);

//		return userRepository.findOneByEmailIgnoreCase(login).map(user -> createSpringSecurityUser(login, user))
//				.orElseThrow(() -> new UsernameNotFoundException(
//						"User with email " + login + " was not found in the database"));

		if (new EmailValidator().isValid(login, null)) {
			return userRepository.findOneWithRolesByEmailIgnoreCase(login)
					.map(user -> createSpringSecurityUser(login, user)).orElseThrow(() -> new UsernameNotFoundException(
							"User with email " + login + " was not found in the database"));
		}

		String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
		return userRepository.findOneWithRolesByLogin(lowercaseLogin)
				.map(user -> createSpringSecurityUser(lowercaseLogin, user))
				.orElseThrow(() -> new UsernameNotFoundException(
						"User " + lowercaseLogin + " was not found in the database"));
	}

	private UserDetails createSpringSecurityUser(String lowercaseLogin, User user) {
		if (!user.isActivated()) {
			throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
		}

		List<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(Role::getCode)
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
				grantedAuthorities);
	}

}
