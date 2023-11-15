package com.example.myapp.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.myapp.constant.RolesConstants;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.repository.UserRepository;
import com.example.myapp.resource.errors.BadRequestAlertException;
import com.example.myapp.resource.errors.LoginAlreadyUsedException;
import com.example.myapp.service.MailService;
import com.example.myapp.service.UserService;
import com.example.myapp.util.HeaderUtil;
import com.example.myapp.util.PaginationUtil;
import com.example.myapp.util.ResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1.0/admin")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

	private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections
			.unmodifiableList(Arrays.asList("id", "fullname", "email", "login", "phoneNumber", "activated", "createdBy",
					"createdDate", "lastModifiedBy", "lastModifiedDate"));

	private final UserService userService;

	private final UserRepository userRepository;

	private final MailService mailService;

	@Value("${application.name}")
	private String applicationName;

	@GetMapping("/users")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<List<UserDTO>> getAllUsers(@org.springdoc.api.annotations.ParameterObject Pageable pageable,
			String filter) {
		log.debug("REST request to get all User for an admin");
		if (!onlyContainsAllowedProperties(pageable)) {
			return ResponseEntity.badRequest().build();
		}

		final Page<UserDTO> page = userService.getAllManagedUsers(filter, pageable);
		HttpHeaders headers = PaginationUtil
				.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	@GetMapping("/users/{login}")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return ResponseUtil.wrapOrNotFound(userService.getUserWithRolesByLogin(login));
	}

	@PostMapping("/users")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
		log.debug("REST request to save User : {}", userDTO);
		if (userDTO.getId() != null) {
			throw new BadRequestAlertException("A new user cannot already have an ID");
		} else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
			throw new LoginAlreadyUsedException();
		} else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
			throw new com.example.myapp.resource.errors.EmailAlreadyUsedException();
		} else {
			UserDTO newUser = userService.createUser(userDTO);
			mailService.sendCreationEmail(newUser);
			return ResponseEntity.created(new URI("/api/admin/users/" + newUser.getEmail()))
					.headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUser.getEmail()))
					.body(newUser);
		}
	}

	@PutMapping("/users")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
		log.debug("REST request to update User : {}", userDTO);
		Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
			throw new com.example.myapp.resource.errors.EmailAlreadyUsedException();
		}
		existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
		if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
			throw new LoginAlreadyUsedException();
		}

		Optional<UserDTO> updatedUser = userService.updateUser(userDTO);

		return ResponseUtil.wrapOrNotFound(updatedUser,
				HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getEmail()));
	}

	@DeleteMapping("/users/{email}")
	@PreAuthorize("hasAuthority(\"" + RolesConstants.ADMIN + "\")")
	public ResponseEntity<Void> deleteUser(@PathVariable String email) {
		log.debug("REST request to delete User: {}", email);
		userService.deleteUser(email);
		return ResponseEntity.noContent()
				.headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", email)).build();
	}

	private boolean onlyContainsAllowedProperties(Pageable pageable) {
		return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
	}

}
