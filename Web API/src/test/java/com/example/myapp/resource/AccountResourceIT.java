//package com.example.myapp.resource;
//
//import static com.example.myapp.resource.AccountResourceIT.TEST_USER_LOGIN;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.example.myapp.IntegrationTest;
//import com.example.myapp.constant.RolesConstants;
//import com.example.myapp.domain.User;
//import com.example.myapp.dto.PasswordChangeDTO;
//import com.example.myapp.dto.UserDTO;
//import com.example.myapp.repository.RoleRepository;
//import com.example.myapp.repository.UserRepository;
//import com.example.myapp.service.UserService;
//import com.example.myapp.util.TestUtil;
//import com.example.myapp.dto.KeyAndPasswordDTO;
//import com.example.myapp.vm.ManagedUserVM;
//
//@AutoConfigureMockMvc
//@WithMockUser(value = TEST_USER_LOGIN)
//@IntegrationTest
//class AccountResourceIT {
//	static final String TEST_USER_LOGIN = "test";
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private RoleRepository roleRepository;
//
//	@Autowired
//	private UserService userService;
//
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//
//	@Autowired
//	private MockMvc restAccountMockMvc;
//
//	@Test
//	@WithUnauthenticatedMockUser
//	void testNonAuthenticatedUser() throws Exception {
//		restAccountMockMvc.perform(get("/api/v1.0/authenticate").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk()).andExpect(content().string(""));
//	}
//
//	@Test
//	void testAuthenticatedUser() throws Exception {
//		restAccountMockMvc.perform(get("/api/v1.0/authenticate").with(request -> {
//			request.setRemoteUser(TEST_USER_LOGIN);
//			return request;
//		}).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(TEST_USER_LOGIN));
//	}
//
//	@Test
//	void testGetExistingAccount() throws Exception {
//		List<String> roles = new ArrayList<>();
//		roles.add(RolesConstants.ADMIN);
//
//		UserDTO user = new UserDTO();
//		user.setLogin(TEST_USER_LOGIN);
//		user.setFullname("thanh");
//		user.setEmail("thanh@sonic.com");
//		user.setImageUrl("http://placehold.it/50x50");
//		user.setRoles(roles);
//		userService.createUser(user);
//
//		restAccountMockMvc.perform(get("/api/v1.0/account").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(jsonPath("$.login").value(TEST_USER_LOGIN))
//				.andExpect(jsonPath("$.fullname").value("thanh"))
//				.andExpect(jsonPath("$.email").value("thanh@sonic.com"))
//				.andExpect(jsonPath("$.roles").value(RolesConstants.ADMIN));
//	}
//
//	@Test
//	void testGetUnknownAccount() throws Exception {
//		restAccountMockMvc.perform(get("/api/v1.0/account").accept(MediaType.APPLICATION_PROBLEM_JSON))
//				.andExpect(status().isInternalServerError());
//	}
//
//	@Test
//	@Transactional
//	void testRegisterValid() throws Exception {
//		ManagedUserVM validUser = new ManagedUserVM();
//		validUser.setLogin("test-register-valid");
//		validUser.setPassword("password");
//		validUser.setFullname("Alice");
//		validUser.setEmail("test-register-valid@example.com");
//		validUser.setImageUrl("http://placehold.it/50x50");
//		validUser.setRoles(Collections.singletonList(RolesConstants.USER));
//		assertThat(userRepository.findOneByLogin("test-register-valid")).isEmpty();
//
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(validUser))).andExpect(status().isCreated());
//
//		assertThat(userRepository.findOneByLogin("test-register-valid")).isPresent();
//	}
//
//	@Test
//	@Transactional
//	void testRegisterInvalidLogin() throws Exception {
//		ManagedUserVM invalidUser = new ManagedUserVM();
//		invalidUser.setLogin("funky-log(n"); // <-- invalid
//		invalidUser.setPassword("password");
//		invalidUser.setFullname("Funky");
//		invalidUser.setEmail("funky@example.com");
//		invalidUser.setActivated(true);
//		invalidUser.setImageUrl("http://placehold.it/50x50");
//		invalidUser.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
//
//		Optional<User> user = userRepository.findOneByEmailIgnoreCase("funky@example.com");
//		assertThat(user).isEmpty();
//	}
//
//	@Test
//	@Transactional
//	void testRegisterInvalidEmail() throws Exception {
//		ManagedUserVM invalidUser = new ManagedUserVM();
//		invalidUser.setLogin("bob");
//		invalidUser.setPassword("password");
//		invalidUser.setFullname("Bob");
//		invalidUser.setEmail("invalid"); // <-- invalid
//		invalidUser.setActivated(true);
//		invalidUser.setImageUrl("http://placehold.it/50x50");
//		invalidUser.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
//
//		Optional<User> user = userRepository.findOneByLogin("bob");
//		assertThat(user).isEmpty();
//	}
//
//	@Test
//	@Transactional
//	void testRegisterInvalidPassword() throws Exception {
//		ManagedUserVM invalidUser = new ManagedUserVM();
//		invalidUser.setLogin("bob");
//		invalidUser.setPassword("123"); // password with only 3 digits
//		invalidUser.setFullname("Bob");
//		invalidUser.setEmail("bob@example.com");
//		invalidUser.setActivated(true);
//		invalidUser.setImageUrl("http://placehold.it/50x50");
//		invalidUser.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
//
//		Optional<User> user = userRepository.findOneByLogin("bob");
//		assertThat(user).isEmpty();
//	}
//
//	@Test
//	@Transactional
//	void testRegisterNullPassword() throws Exception {
//		ManagedUserVM invalidUser = new ManagedUserVM();
//		invalidUser.setLogin("bob");
//		invalidUser.setPassword(null); // invalid null password
//		invalidUser.setFullname("Bob");
//		invalidUser.setEmail("bob@example.com");
//		invalidUser.setActivated(true);
//		invalidUser.setImageUrl("http://placehold.it/50x50");
//		invalidUser.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(invalidUser))).andExpect(status().isBadRequest());
//
//		Optional<User> user = userRepository.findOneByLogin("bob");
//		assertThat(user).isEmpty();
//	}
//
//	@Test
//	@Transactional
//	void testRegisterDuplicateLogin() throws Exception {
//		// First registration
//		ManagedUserVM firstUser = new ManagedUserVM();
//		firstUser.setLogin("alice");
//		firstUser.setPassword("password");
//		firstUser.setFullname("Alice");
//		firstUser.setEmail("alice@example.com");
//		firstUser.setImageUrl("http://placehold.it/50x50");
//		firstUser.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		// Duplicate login, different email
//		ManagedUserVM secondUser = new ManagedUserVM();
//		secondUser.setLogin(firstUser.getLogin());
//		secondUser.setPassword(firstUser.getPassword());
//		secondUser.setFullname(firstUser.getFullname());
//		secondUser.setEmail("alice2@example.com");
//		secondUser.setImageUrl(firstUser.getImageUrl());
//		secondUser.setCreatedBy(firstUser.getCreatedBy());
//		secondUser.setCreatedDate(firstUser.getCreatedDate());
//		secondUser.setLastModifiedBy(firstUser.getLastModifiedBy());
//		secondUser.setLastModifiedDate(firstUser.getLastModifiedDate());
//		secondUser.setRoles(new ArrayList<>(firstUser.getRoles()));
//
//		// First user
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(firstUser))).andExpect(status().isCreated());
//
//		// Second (non activated) user
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(secondUser))).andExpect(status().isCreated());
//
//		Optional<User> testUser = userRepository.findOneByEmailIgnoreCase("alice2@example.com");
//		assertThat(testUser).isPresent();
//		testUser.get().setActivated(true);
//		userRepository.save(testUser.get());
//
//		// Second (already activated) user
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(secondUser))).andExpect(status().is4xxClientError());
//	}
//
//	@Test
//	@Transactional
//	void testRegisterDuplicateEmail() throws Exception {
//		// First user
//		ManagedUserVM firstUser = new ManagedUserVM();
//		firstUser.setLogin("test-register-duplicate-email");
//		firstUser.setPassword("password");
//		firstUser.setFullname("Alice");
//		firstUser.setEmail("test-register-duplicate-email@example.com");
//		firstUser.setImageUrl("http://placehold.it/50x50");
//		firstUser.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		// Register first user
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(firstUser))).andExpect(status().isCreated());
//
//		Optional<User> testUser1 = userRepository.findOneByLogin("test-register-duplicate-email");
//		assertThat(testUser1).isPresent();
//
//		// Duplicate email, different login
//		ManagedUserVM secondUser = new ManagedUserVM();
//		secondUser.setLogin("test-register-duplicate-email-2");
//		secondUser.setPassword(firstUser.getPassword());
//		secondUser.setFullname(firstUser.getFullname());
//		secondUser.setEmail(firstUser.getEmail());
//		secondUser.setImageUrl(firstUser.getImageUrl());
//		secondUser.setRoles(new ArrayList<>(firstUser.getRoles()));
//
//		// Register second (non activated) user
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(secondUser))).andExpect(status().isCreated());
//
//		Optional<User> testUser2 = userRepository.findOneByLogin("test-register-duplicate-email");
//		assertThat(testUser2).isEmpty();
//
//		Optional<User> testUser3 = userRepository.findOneByLogin("test-register-duplicate-email-2");
//		assertThat(testUser3).isPresent();
//
//		// Duplicate email - with uppercase email address
//		ManagedUserVM userWithUpperCaseEmail = new ManagedUserVM();
//		userWithUpperCaseEmail.setId(firstUser.getId());
//		userWithUpperCaseEmail.setLogin("test-register-duplicate-email-3");
//		userWithUpperCaseEmail.setPassword(firstUser.getPassword());
//		userWithUpperCaseEmail.setFullname(firstUser.getFullname());
//		userWithUpperCaseEmail.setEmail("TEST-register-duplicate-email@example.com");
//		userWithUpperCaseEmail.setImageUrl(firstUser.getImageUrl());
//		userWithUpperCaseEmail.setRoles(new ArrayList<>(firstUser.getRoles()));
//
//		// Register third (not activated) user
//		restAccountMockMvc
//				.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//						.content(TestUtil.convertObjectToJsonBytes(userWithUpperCaseEmail)))
//				.andExpect(status().isCreated());
//
//		Optional<User> testUser4 = userRepository.findOneByLogin("test-register-duplicate-email-3");
//		assertThat(testUser4).isPresent();
//		assertThat(testUser4.get().getEmail()).isEqualTo("test-register-duplicate-email@example.com");
//
//		testUser4.get().setActivated(true);
//		userService.updateUser((new UserDTO(testUser4.get())));
//
//		// Register 4th (already activated) user
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(secondUser))).andExpect(status().is4xxClientError());
//	}
//
//	@Test
//	@Transactional
//	void testRegisterAdminIsIgnored() throws Exception {
//		ManagedUserVM validUser = new ManagedUserVM();
//		validUser.setLogin("badguy");
//		validUser.setPassword("password");
//		validUser.setFullname("Bad");
//		validUser.setEmail("badguy@example.com");
//		validUser.setActivated(true);
//		validUser.setImageUrl("http://placehold.it/50x50");
//		validUser.setRoles(Collections.singletonList(RolesConstants.ADMIN));
//
//		restAccountMockMvc.perform(post("/api/v1.0/register").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(validUser))).andExpect(status().isCreated());
//
//		Optional<User> userDup = userRepository.findOneWithRolesByLogin("badguy");
//		assertThat(userDup).isPresent();
//		assertThat(userDup.get().getRoles()).hasSize(1)
//				.containsExactly(roleRepository.findOneByCode(RolesConstants.USER).get());
//	}
//
//	@Test
//	@Transactional
//	void testActivateAccount() throws Exception {
//		final String activationKey = "some activation key";
//		User user = new User();
//		user.setLogin("activate-account");
//		user.setEmail("activate-account@example.com");
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setActivated(false);
//		user.setActivationKey(activationKey);
//
//		userRepository.saveAndFlush(user);
//
//		restAccountMockMvc.perform(get("/api/v1.0/activate?key={activationKey}", activationKey))
//				.andExpect(status().isOk());
//
//		user = userRepository.findOneByLogin(user.getLogin()).orElse(null);
//		assertThat(user.isActivated()).isTrue();
//	}
//
//	@Test
//	@Transactional
//	void testActivateAccountWithWrongKey() throws Exception {
//		restAccountMockMvc.perform(get("/api/v1.0/activate?key=wrongActivationKey"))
//				.andExpect(status().isInternalServerError());
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("save-account")
//	void testSaveAccount() throws Exception {
//		User user = new User();
//		user.setLogin("save-account");
//		user.setEmail("save-account@example.com");
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setActivated(true);
//		userRepository.saveAndFlush(user);
//
//		UserDTO userDTO = new UserDTO();
//		userDTO.setLogin("not-used");
//		userDTO.setFullname("firstname");
//		userDTO.setEmail("save-account@example.com");
//		userDTO.setActivated(false);
//		userDTO.setImageUrl("http://placehold.it/50x50");
//		userDTO.setRoles(Collections.singletonList(RolesConstants.ADMIN));
//
//		restAccountMockMvc.perform(post("/api/v1.0/account").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(userDTO))).andExpect(status().isOk());
//
//		User updatedUser = userRepository.findOneWithRolesByLogin(user.getLogin()).orElse(null);
//		assertThat(updatedUser.getFullname()).isEqualTo(userDTO.getFullname());
//		assertThat(updatedUser.getEmail()).isEqualTo(userDTO.getEmail());
//		assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
//		assertThat(updatedUser.getImageUrl()).isEqualTo(userDTO.getImageUrl());
//		assertThat(updatedUser.isActivated()).isTrue();
//		assertThat(updatedUser.getRoles()).isEmpty();
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("save-existing-email")
//	void testSaveExistingEmail() throws Exception {
//		User user = new User();
//		user.setLogin("save-existing-email");
//		user.setEmail("save-existing-email@example.com");
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setActivated(true);
//		userRepository.saveAndFlush(user);
//
//		User anotherUser = new User();
//		anotherUser.setLogin("save-existing-email2");
//		anotherUser.setEmail("save-existing-email2@example.com");
//		anotherUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		anotherUser.setActivated(true);
//
//		userRepository.saveAndFlush(anotherUser);
//
//		UserDTO userDTO = new UserDTO();
//		userDTO.setLogin("not-used");
//		userDTO.setFullname("firstname");
//		userDTO.setEmail("save-existing-email2@example.com");
//		userDTO.setActivated(false);
//		userDTO.setImageUrl("http://placehold.it/50x50");
//		userDTO.setRoles(Collections.singletonList(RolesConstants.ADMIN));
//
//		restAccountMockMvc.perform(post("/api/v1.0/account").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(userDTO))).andExpect(status().isBadRequest());
//
//		User updatedUser = userRepository.findOneByLogin("save-existing-email").orElse(null);
//		assertThat(updatedUser.getEmail()).isEqualTo("save-existing-email@example.com");
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("save-existing-email-and-login")
//	void testSaveExistingEmailAndLogin() throws Exception {
//		User user = new User();
//		user.setLogin("save-existing-email-and-login");
//		user.setEmail("save-existing-email-and-login@example.com");
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setActivated(true);
//		userRepository.saveAndFlush(user);
//
//		UserDTO userDTO = new UserDTO();
//		userDTO.setLogin("not-used");
//		userDTO.setFullname("firstname");
//		userDTO.setEmail("save-existing-email-and-login@example.com");
//		userDTO.setActivated(false);
//		userDTO.setImageUrl("http://placehold.it/50x50");
//		userDTO.setRoles(Collections.singletonList(RolesConstants.ADMIN));
//
//		restAccountMockMvc.perform(post("/api/v1.0/account").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(userDTO))).andExpect(status().isOk());
//
//		User updatedUser = userRepository.findOneByLogin("save-existing-email-and-login").orElse(null);
//		assertThat(updatedUser.getEmail()).isEqualTo("save-existing-email-and-login@example.com");
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("change-password-wrong-existing-password")
//	void testChangePasswordWrongExistingPassword() throws Exception {
//		User user = new User();
//		String currentPassword = RandomStringUtils.randomAlphanumeric(60);
//		user.setPassword(passwordEncoder.encode(currentPassword));
//		user.setLogin("change-password-wrong-existing-password");
//		user.setEmail("change-password-wrong-existing-password@example.com");
//		userRepository.saveAndFlush(user);
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/change-password").contentType(MediaType.APPLICATION_JSON)
//						.content(TestUtil.convertObjectToJsonBytes(
//								new PasswordChangeDTO("1" + currentPassword, "new password"))))
//				.andExpect(status().isBadRequest());
//
//		User updatedUser = userRepository.findOneByLogin("change-password-wrong-existing-password").orElse(null);
//		assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isFalse();
//		assertThat(passwordEncoder.matches(currentPassword, updatedUser.getPassword())).isTrue();
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("change-password")
//	void testChangePassword() throws Exception {
//		User user = new User();
//		String currentPassword = RandomStringUtils.randomAlphanumeric(60);
//		user.setPassword(passwordEncoder.encode(currentPassword));
//		user.setLogin("change-password");
//		user.setEmail("change-password@example.com");
//		userRepository.saveAndFlush(user);
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/change-password").contentType(MediaType.APPLICATION_JSON).content(
//						TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, "new password"))))
//				.andExpect(status().isOk());
//
//		User updatedUser = userRepository.findOneByLogin("change-password").orElse(null);
//		assertThat(passwordEncoder.matches("new password", updatedUser.getPassword())).isTrue();
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("change-password-too-small")
//	void testChangePasswordTooSmall() throws Exception {
//		User user = new User();
//		String currentPassword = RandomStringUtils.randomAlphanumeric(60);
//		user.setPassword(passwordEncoder.encode(currentPassword));
//		user.setLogin("change-password-too-small");
//		user.setEmail("change-password-too-small@example.com");
//		userRepository.saveAndFlush(user);
//
//		String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MIN_LENGTH - 1);
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/change-password").contentType(MediaType.APPLICATION_JSON).content(
//						TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
//				.andExpect(status().isBadRequest());
//
//		User updatedUser = userRepository.findOneByLogin("change-password-too-small").orElse(null);
//		assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("change-password-too-long")
//	void testChangePasswordTooLong() throws Exception {
//		User user = new User();
//		String currentPassword = RandomStringUtils.randomAlphanumeric(60);
//		user.setPassword(passwordEncoder.encode(currentPassword));
//		user.setLogin("change-password-too-long");
//		user.setEmail("change-password-too-long@example.com");
//		userRepository.saveAndFlush(user);
//
//		String newPassword = RandomStringUtils.random(ManagedUserVM.PASSWORD_MAX_LENGTH + 1);
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/change-password").contentType(MediaType.APPLICATION_JSON).content(
//						TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, newPassword))))
//				.andExpect(status().isBadRequest());
//
//		User updatedUser = userRepository.findOneByLogin("change-password-too-long").orElse(null);
//		assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
//	}
//
//	@Test
//	@Transactional
//	@WithMockUser("change-password-empty")
//	void testChangePasswordEmpty() throws Exception {
//		User user = new User();
//		String currentPassword = RandomStringUtils.randomAlphanumeric(60);
//		user.setPassword(passwordEncoder.encode(currentPassword));
//		user.setLogin("change-password-empty");
//		user.setEmail("change-password-empty@example.com");
//		userRepository.saveAndFlush(user);
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/change-password").contentType(MediaType.APPLICATION_JSON)
//						.content(TestUtil.convertObjectToJsonBytes(new PasswordChangeDTO(currentPassword, ""))))
//				.andExpect(status().isBadRequest());
//
//		User updatedUser = userRepository.findOneByLogin("change-password-empty").orElse(null);
//		assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
//	}
//
//	@Test
//    @Transactional
//    void testRequestPasswordReset() throws Exception {
//        User user = new User();
//        user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//        user.setActivated(true);
//        user.setLogin("password-reset");
//        user.setEmail("password-reset@example.com");
//        userRepository.saveAndFlush(user);
//
//        restAccountMockMvc
//            .perform(post("/api/v1.0/account/reset-password/init").content("password-reset@example.com"))
//            .andExpect(status().isOk());
//    }
//
//	@Test
//	@Transactional
//	void testRequestPasswordResetUpperCaseEmail() throws Exception {
//		User user = new User();
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setActivated(true);
//		user.setLogin("password-reset-upper-case");
//		user.setEmail("password-reset-upper-case@example.com");
//		userRepository.saveAndFlush(user);
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/reset-password/init").content("password-reset-upper-case@EXAMPLE.COM"))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	void testRequestPasswordResetWrongEmail() throws Exception {
//		restAccountMockMvc
//				.perform(
//						post("/api/v1.0/account/reset-password/init").content("password-reset-wrong-email@example.com"))
//				.andExpect(status().isOk());
//	}
//
//	@Test
//	@Transactional
//	void testFinishPasswordReset() throws Exception {
//		User user = new User();
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setLogin("finish-password-reset");
//		user.setEmail("finish-password-reset@example.com");
//		user.setResetDate(Instant.now().plusSeconds(60));
//		user.setResetKey("reset key");
//		userRepository.saveAndFlush(user);
//
//		KeyAndPasswordDTO keyAndPassword = new KeyAndPasswordDTO();
//		keyAndPassword.setKey(user.getResetKey());
//		keyAndPassword.setNewPassword("new password");
//
//		restAccountMockMvc.perform(post("/api/v1.0/account/reset-password/finish")
//				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
//				.andExpect(status().isOk());
//
//		User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
//		assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isTrue();
//	}
//
//	@Test
//	@Transactional
//	void testFinishPasswordResetTooSmall() throws Exception {
//		User user = new User();
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setLogin("finish-password-reset-too-small");
//		user.setEmail("finish-password-reset-too-small@example.com");
//		user.setResetDate(Instant.now().plusSeconds(60));
//		user.setResetKey("reset key too small");
//		userRepository.saveAndFlush(user);
//
//		KeyAndPasswordDTO keyAndPassword = new KeyAndPasswordDTO();
//		keyAndPassword.setKey(user.getResetKey());
//		keyAndPassword.setNewPassword("foo");
//
//		restAccountMockMvc.perform(post("/api/v1.0/account/reset-password/finish")
//				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
//				.andExpect(status().isBadRequest());
//
//		User updatedUser = userRepository.findOneByLogin(user.getLogin()).orElse(null);
//		assertThat(passwordEncoder.matches(keyAndPassword.getNewPassword(), updatedUser.getPassword())).isFalse();
//	}
//
//	@Test
//	@Transactional
//	void testFinishPasswordResetWrongKey() throws Exception {
//		KeyAndPasswordDTO keyAndPassword = new KeyAndPasswordDTO();
//		keyAndPassword.setKey("wrong reset key");
//		keyAndPassword.setNewPassword("new password");
//
//		restAccountMockMvc
//				.perform(post("/api/v1.0/account/reset-password/finish").contentType(MediaType.APPLICATION_JSON)
//						.content(TestUtil.convertObjectToJsonBytes(keyAndPassword)))
//				.andExpect(status().isInternalServerError());
//	}
//}
