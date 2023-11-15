package com.example.myapp.resource;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.myapp.domain.User;
import com.example.myapp.dto.KeyAndPasswordDTO;
import com.example.myapp.dto.PasswordChangeDTO;
import com.example.myapp.dto.RegisterDTO;
import com.example.myapp.dto.ResetPasswordDTO;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.repository.UserRepository;
import com.example.myapp.service.MailService;
import com.example.myapp.service.UserService;
import com.example.myapp.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1.0")

public class AccountResource {

	private static class AccountResourceException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private AccountResourceException(String message) {
			super(message);
		}
	}

	private final UserRepository userRepository;

	private final UserService userService;

	private final MailService mailService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public void registerAccount(@Valid @RequestBody RegisterDTO registerDTO) {
		if (isPasswordLengthInvalid(registerDTO.getPassword())) {
			throw new com.example.myapp.resource.errors.InvalidPasswordException();
		}
		User user = userService.registerUser(registerDTO, registerDTO.getPassword());
		mailService.sendActivationEmail(user);
	}

	@GetMapping("/authenticate")
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	@GetMapping("/activate")
	public void activateAccount(@RequestParam(value = "key") String key) {
		Optional<User> user = userService.activateRegistration(key);
		if (!user.isPresent()) {
			throw new AccountResourceException("No user was found for this activation key");
		}
	}

	@GetMapping("/account")
	public UserDTO getAccount() {
		return userService.getUserWithRoles().map(UserDTO::new)
				.orElseThrow(() -> new AccountResourceException("User could not be found"));
	}

	@PostMapping("/account")
	public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
		String userLogin = SecurityUtil.getCurrentUserLogin()
				.orElseThrow(() -> new AccountResourceException("Current user login not found"));
		Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
		if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
			throw new com.example.myapp.resource.errors.EmailAlreadyUsedException();
		}

		Optional<User> user = userRepository.findOneByLogin(userLogin);
		if (!user.isPresent()) {
			throw new AccountResourceException("User could not be found");
		}

		userService.updateUser(userDTO.getFullname(), userDTO.getEmail(), userDTO.getPhoneNumber(),
				userDTO.getImageUrl());
	}

	@PostMapping(path = "/account/change-password")
	public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
		if (isPasswordLengthInvalid(passwordChangeDto.getNewPassword())) {
			throw new com.example.myapp.resource.errors.InvalidPasswordException();
		}
		userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
	}

	@PostMapping(path = "/account/reset-password/init")
	public void requestPasswordReset(@RequestBody ResetPasswordDTO resetPasswordDTO) {
		Optional<User> user = userService.requestPasswordReset(resetPasswordDTO.getEmail());
		if (user.isPresent()) {
			mailService.sendPasswordResetMail(user.get(), resetPasswordDTO.getResetPasswordUrl());
		} else {
			log.warn("Password reset requested for non existing mail");
		}
	}

	@PostMapping(path = "/account/reset-password/finish")
	public void finishPasswordReset(@RequestBody KeyAndPasswordDTO keyAndPassword) {
		if (isPasswordLengthInvalid(keyAndPassword.getNewPassword())) {
			throw new com.example.myapp.resource.errors.InvalidPasswordException();
		}
		Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(),
				keyAndPassword.getKey());

		if (!user.isPresent()) {
			throw new AccountResourceException("No user was found for this reset key");
		}
	}

	private static boolean isPasswordLengthInvalid(String password) {
		return (StringUtils.isEmpty(password) || password.length() < RegisterDTO.PASSWORD_MIN_LENGTH
				|| password.length() > RegisterDTO.PASSWORD_MAX_LENGTH);
	}

}
