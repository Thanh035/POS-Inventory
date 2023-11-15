//package com.example.myapp.resource;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.function.Consumer;
//
//import javax.persistence.EntityManager;
//
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.example.myapp.IntegrationTest;
//import com.example.myapp.constant.RolesConstants;
//import com.example.myapp.domain.Role;
//import com.example.myapp.domain.User;
//import com.example.myapp.dto.UserDTO;
//import com.example.myapp.mapper.UserMapper;
//import com.example.myapp.repository.UserRepository;
//import com.example.myapp.util.TestUtil;
//import com.example.myapp.vm.ManagedUserVM;
//
//@AutoConfigureMockMvc
//@WithMockUser(authorities = RolesConstants.ADMIN)
//@IntegrationTest
//class UserResourceIT {
//
//	private static final String DEFAULT_LOGIN = "thanh";
//	private static final String UPDATED_LOGIN = "sonic";
//
//	private static final String DEFAULT_PASSWORD = "passthanh";
//	private static final String UPDATED_PASSWORD = "passsonic";
//
//	private static final String DEFAULT_EMAIL = "thanh@localhost";
//	private static final String UPDATED_EMAIL = "sonic@localhost";
//
//	private static final String DEFAULT_FULLNAME = "quang thanh";
//	private static final String UPDATED_FULLNAME = "thanh quang";
//
//	private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
//	private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";
//
//	private static final Long DEFAULT_ID = 1L;
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private UserMapper userMapper;
//
//	@Autowired
//	private EntityManager em;
//
//	@Autowired
//	private MockMvc restUserMockMvc;
//
//	private User user;
//
//	public static User createEntity(EntityManager em) {
//		User user = new User();
//		user.setLogin(DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5));
//		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		user.setActivated(true);
//		user.setEmail(RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL);
//		user.setFullname(DEFAULT_FULLNAME);
//		user.setImageUrl(DEFAULT_IMAGEURL);
//		return user;
//	}
//
//	public static User initTestUser(UserRepository userRepository, EntityManager em) {
//		userRepository.deleteAll();
//		User user = createEntity(em);
//
//		user.setLogin(DEFAULT_LOGIN);
//		user.setEmail(DEFAULT_EMAIL);
//
//		return user;
//	}
//
//	@BeforeEach
//	public void initTest() {
//		user = initTestUser(userRepository, em);
//	}
//
//	@Test
//	@Transactional
//	void createUser() throws Exception {
//		int databaseSizeBeforeCreate = userRepository.findAll().size();
//
//		// Create the User
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setLogin(DEFAULT_LOGIN);
//		managedUserVM.setPassword(DEFAULT_PASSWORD);
//		managedUserVM.setFullname(DEFAULT_FULLNAME);
//		managedUserVM.setEmail(DEFAULT_EMAIL);
//		managedUserVM.setActivated(true);
//		managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restUserMockMvc.perform(post("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isCreated());
//
//		// Validate the User in the database
//		assertPersistedUsers(users -> {
//			assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
//			User testUser = users.get(users.size() - 1);
//			assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
//			assertThat(testUser.getFullname()).isEqualTo(DEFAULT_FULLNAME);
//			assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
//			assertThat(testUser.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
//		});
//	}
//
//	@Test
//	@Transactional
//	void createUserWithExistingId() throws Exception {
//		int databaseSizeBeforeCreate = userRepository.findAll().size();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setId(DEFAULT_ID);
//		managedUserVM.setLogin(DEFAULT_LOGIN);
//		managedUserVM.setPassword(DEFAULT_PASSWORD);
//		managedUserVM.setFullname(DEFAULT_FULLNAME);
//		managedUserVM.setEmail(DEFAULT_EMAIL);
//		managedUserVM.setActivated(true);
//		managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		// An entity with an existing ID cannot be created, so this API call must fail
//		restUserMockMvc.perform(post("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
//
//		// Validate the User in the database
//		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
//	}
//
//	@Test
//	@Transactional
//	void createUserWithExistingLogin() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//		int databaseSizeBeforeCreate = userRepository.findAll().size();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setLogin(DEFAULT_LOGIN); // this login should already be used
//		managedUserVM.setPassword(DEFAULT_PASSWORD);
//		managedUserVM.setFullname(DEFAULT_FULLNAME);
//		managedUserVM.setEmail("anothermail@localhost");
//		managedUserVM.setActivated(true);
//		managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		// Create the User
//		restUserMockMvc.perform(post("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
//
//		// Validate the User in the database
//		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
//	}
//
//	@Test
//	@Transactional
//	void createUserWithExistingEmail() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//		int databaseSizeBeforeCreate = userRepository.findAll().size();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setLogin("anotherlogin");
//		managedUserVM.setPassword(DEFAULT_PASSWORD);
//		managedUserVM.setFullname(DEFAULT_FULLNAME);
//		managedUserVM.setEmail(DEFAULT_EMAIL); // this email should already be used
//		managedUserVM.setActivated(true);
//		managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		// Create the User
//		restUserMockMvc.perform(post("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
//
//		// Validate the User in the database
//		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
//	}
//
//	@Test
//	@Transactional
//	void getAllUsers() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//
//		// Get all the users
//		restUserMockMvc.perform(get("/api/v1.0/admin/users?sort=id,desc").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
//				.andExpect(jsonPath("$.[*].fullname").value(hasItem(DEFAULT_FULLNAME)))
//				.andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
//				.andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGEURL)));
//	}
//
//	@Test
//	@Transactional
//	void getUser() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//
//		// Get the user
//		restUserMockMvc.perform(get("/api/v1.0/admin/users/{login}", user.getLogin())).andExpect(status().isOk())
//				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(jsonPath("$.login").value(user.getLogin()))
//				.andExpect(jsonPath("$.fullname").value(DEFAULT_FULLNAME))
//				.andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
//				.andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGEURL));
//	}
//
////	@Test
////	@Transactional
////	void getNonExistingUser() throws Exception {
////		restUserMockMvc.perform(get("/api/v1.0/admin/users/unknown")).andExpect(status().isNotFound());
////	}
//
//	@Test
//	@Transactional
//	void updateUser() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//		int databaseSizeBeforeUpdate = userRepository.findAll().size();
//
//		// Update the user
//		User updatedUser = userRepository.findById(user.getId()).get();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setId(updatedUser.getId());
//		managedUserVM.setLogin(updatedUser.getLogin());
//		managedUserVM.setPassword(UPDATED_PASSWORD);
//		managedUserVM.setFullname(UPDATED_FULLNAME);
//		managedUserVM.setEmail(UPDATED_EMAIL);
//		managedUserVM.setActivated(updatedUser.isActivated());
//		managedUserVM.setImageUrl(UPDATED_IMAGEURL);
//		managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
//		managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
//		managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
//		managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restUserMockMvc.perform(put("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isOk());
//
//		// Validate the User in the database
//		assertPersistedUsers(users -> {
//			assertThat(users).hasSize(databaseSizeBeforeUpdate);
//			User testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId())).findFirst().get();
//			assertThat(testUser.getFullname()).isEqualTo(UPDATED_FULLNAME);
//			assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
//			assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
//		});
//	}
//
//	@Test
//	@Transactional
//	void updateUserLogin() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//		int databaseSizeBeforeUpdate = userRepository.findAll().size();
//
//		// Update the user
//		User updatedUser = userRepository.findById(user.getId()).get();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setId(updatedUser.getId());
//		managedUserVM.setLogin(UPDATED_LOGIN);
//		managedUserVM.setPassword(UPDATED_PASSWORD);
//		managedUserVM.setFullname(UPDATED_FULLNAME);
//		managedUserVM.setEmail(UPDATED_EMAIL);
//		managedUserVM.setActivated(updatedUser.isActivated());
//		managedUserVM.setImageUrl(UPDATED_IMAGEURL);
//		managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
//		managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
//		managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
//		managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restUserMockMvc.perform(put("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isOk());
//
//		// Validate the User in the database
//		assertPersistedUsers(users -> {
//			assertThat(users).hasSize(databaseSizeBeforeUpdate);
//			User testUser = users.stream().filter(usr -> usr.getId().equals(updatedUser.getId())).findFirst().get();
//			assertThat(testUser.getLogin()).isEqualTo(UPDATED_LOGIN);
//			assertThat(testUser.getFullname()).isEqualTo(UPDATED_FULLNAME);
//			assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
//			assertThat(testUser.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
//		});
//	}
//
//	@Test
//	@Transactional
//	void updateUserExistingEmail() throws Exception {
//		// Initialize the database with 2 users
//		userRepository.saveAndFlush(user);
//
//		User anotherUser = new User();
//		anotherUser.setLogin("sonic");
//		anotherUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		anotherUser.setActivated(true);
//		anotherUser.setEmail("sonic@localhost");
//		anotherUser.setFullname("thanh");
//		anotherUser.setImageUrl("");
//		userRepository.saveAndFlush(anotherUser);
//
//		// Update the user
//		User updatedUser = userRepository.findById(user.getId()).get();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setId(updatedUser.getId());
//		managedUserVM.setLogin(updatedUser.getLogin());
//		managedUserVM.setPassword(updatedUser.getPassword());
//		managedUserVM.setFullname(updatedUser.getFullname());
//		managedUserVM.setEmail("sonic@localhost"); // this email should already be used by anotherUser
//		managedUserVM.setActivated(updatedUser.isActivated());
//		managedUserVM.setImageUrl(updatedUser.getImageUrl());
//		managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
//		managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
//		managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
//		managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restUserMockMvc.perform(put("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
//	}
//
//	@Test
//	@Transactional
//	void updateUserExistingLogin() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//
//		User anotherUser = new User();
//		anotherUser.setLogin("sonic");
//		anotherUser.setPassword(RandomStringUtils.randomAlphanumeric(60));
//		anotherUser.setActivated(true);
//		anotherUser.setEmail("sonic@localhost");
//		anotherUser.setFullname("thanh");
//		anotherUser.setImageUrl("");
//		userRepository.saveAndFlush(anotherUser);
//
//		// Update the user
//		User updatedUser = userRepository.findById(user.getId()).get();
//
//		ManagedUserVM managedUserVM = new ManagedUserVM();
//		managedUserVM.setId(updatedUser.getId());
//		managedUserVM.setLogin("sonic"); // this login should already be used by anotherUser
//		managedUserVM.setPassword(updatedUser.getPassword());
//		managedUserVM.setFullname(updatedUser.getFullname());
//		managedUserVM.setEmail(updatedUser.getEmail());
//		managedUserVM.setActivated(updatedUser.isActivated());
//		managedUserVM.setImageUrl(updatedUser.getImageUrl());
//		managedUserVM.setCreatedBy(updatedUser.getCreatedBy());
//		managedUserVM.setCreatedDate(updatedUser.getCreatedDate());
//		managedUserVM.setLastModifiedBy(updatedUser.getLastModifiedBy());
//		managedUserVM.setLastModifiedDate(updatedUser.getLastModifiedDate());
//		managedUserVM.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		restUserMockMvc.perform(put("/api/v1.0/admin/users").contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtil.convertObjectToJsonBytes(managedUserVM))).andExpect(status().isBadRequest());
//	}
//
//	@Test
//	@Transactional
//	void deleteUser() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//		int databaseSizeBeforeDelete = userRepository.findAll().size();
//
//		// Delete the user
//		restUserMockMvc
//				.perform(delete("/api/v1.0/admin/users/{email}", user.getEmail()).accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isNoContent());
//
//		// Validate the database is empty
//		assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
//	}
//
//	@Test
//	void testUserEquals() throws Exception {
//		TestUtil.equalsVerifier(User.class);
//		User user1 = new User();
//		user1.setId(DEFAULT_ID);
//		User user2 = new User();
//		user2.setId(user1.getId());
//		assertThat(user1).isEqualTo(user2);
//		user2.setId(2L);
//		assertThat(user1).isNotEqualTo(user2);
//		user1.setId(null);
//		assertThat(user1).isNotEqualTo(user2);
//	}
//
//	@Test
//	void testUserDTOtoUser() {
//		UserDTO userDTO = new UserDTO();
//		userDTO.setId(DEFAULT_ID);
//		userDTO.setLogin(DEFAULT_LOGIN);
//		userDTO.setFullname(DEFAULT_FULLNAME);
//		userDTO.setEmail(DEFAULT_EMAIL);
//		userDTO.setActivated(true);
//		userDTO.setImageUrl(DEFAULT_IMAGEURL);
//		userDTO.setCreatedBy(DEFAULT_LOGIN);
//		userDTO.setLastModifiedBy(DEFAULT_LOGIN);
//		userDTO.setRoles(Collections.singletonList(RolesConstants.USER));
//
//		User user = userMapper.userDTOToUser(userDTO);
//		assertThat(user.getId()).isEqualTo(DEFAULT_ID);
//		assertThat(user.getLogin()).isEqualTo(DEFAULT_LOGIN);
//		assertThat(user.getFullname()).isEqualTo(DEFAULT_FULLNAME);
//		assertThat(user.getEmail()).isEqualTo(DEFAULT_EMAIL);
//		assertThat(user.isActivated()).isTrue();
//		assertThat(user.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
//		assertThat(user.getCreatedBy()).isNull();
//		assertThat(user.getCreatedDate()).isNotNull();
//		assertThat(user.getLastModifiedBy()).isNull();
//		assertThat(user.getLastModifiedDate()).isNotNull();
//		assertThat(user.getRoles()).extracting("code").containsExactly(RolesConstants.USER);
//	}
//
//	@Test
//	void testUserToUserDTO() {
//		user.setId(DEFAULT_ID);
//		user.setCreatedBy(DEFAULT_LOGIN);
//		user.setCreatedDate(Instant.now());
//		user.setLastModifiedBy(DEFAULT_LOGIN);
//		user.setLastModifiedDate(Instant.now());
//
//		List<Role> roles = new ArrayList<>();
//		Role role = new Role();
//		role.setCode(RolesConstants.USER);
//		roles.add(role);
//		user.setRoles(roles);
//
//		UserDTO userDTO = userMapper.userToUserDTO(user);
//
//		assertThat(userDTO.getId()).isEqualTo(DEFAULT_ID);
//		assertThat(userDTO.getLogin()).isEqualTo(DEFAULT_LOGIN);
//		assertThat(userDTO.getFullname()).isEqualTo(DEFAULT_FULLNAME);
//		assertThat(userDTO.getEmail()).isEqualTo(DEFAULT_EMAIL);
//		assertThat(userDTO.isActivated()).isTrue();
//		assertThat(userDTO.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
//		assertThat(userDTO.getCreatedBy()).isEqualTo(DEFAULT_LOGIN);
//		assertThat(userDTO.getCreatedDate()).isEqualTo(user.getCreatedDate());
//		assertThat(userDTO.getLastModifiedBy()).isEqualTo(DEFAULT_LOGIN);
//		assertThat(userDTO.getLastModifiedDate()).isEqualTo(user.getLastModifiedDate());
//		assertThat(userDTO.getRoles()).containsExactly(RolesConstants.USER);
//		assertThat(userDTO.toString()).isNotNull();
//	}
//
//	@Test
//	void testRoleEquals() {
//		Role roleA = new Role();
//		assertThat(roleA).isNotEqualTo(null).isNotEqualTo(new Object());
//		assertThat(roleA.hashCode()).isZero();
//		assertThat(roleA.toString()).isNotNull();
//
//		Role roleB = new Role();
//		assertThat(roleA).isEqualTo(roleB);
//
//		roleB.setCode(RolesConstants.ADMIN);
//		roleB.setName(RolesConstants.ADMIN);
//		assertThat(roleA).isNotEqualTo(roleB);
//
//		roleA.setCode(RolesConstants.USER);
//		roleA.setName(RolesConstants.USER);
//		assertThat(roleA).isNotEqualTo(roleB);
//
//		roleB.setCode(RolesConstants.USER);
//		roleB.setName(RolesConstants.USER);
//		assertThat(roleA).isEqualTo(roleB).hasSameHashCodeAs(roleB);
//	}
//
//	private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
//		userAssertion.accept(userRepository.findAll());
//	}
//
//}
