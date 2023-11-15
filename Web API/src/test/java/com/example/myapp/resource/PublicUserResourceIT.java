//package com.example.myapp.resource;
//
//import static org.hamcrest.Matchers.hasItem;
//import static org.hamcrest.Matchers.hasItems;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import javax.persistence.EntityManager;
//
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
//import com.example.myapp.domain.User;
//import com.example.myapp.repository.UserRepository;
//
//@AutoConfigureMockMvc
//@WithMockUser(authorities = RolesConstants.ADMIN)
//@IntegrationTest
//class PublicUserResourceIT {
//
//	private static final String DEFAULT_LOGIN = "thanh";
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private EntityManager em;
//
//	@Autowired
//	private MockMvc restUserMockMvc;
//
//	private User user;
//
//	@BeforeEach
//	public void initTest() {
//		user = UserResourceIT.initTestUser(userRepository, em);
//	}
//
//	@Test
//	@Transactional
//	void getAllPublicUsers() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//
//		// Get all the users
//		restUserMockMvc.perform(get("/api/v1.0/users?sort=id,desc").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)));
////				.andExpect(jsonPath("$.[*].email").doesNotExist())
////				.andExpect(jsonPath("$.[*].imageUrl").doesNotExist());
//	}
//
//	@Test
//	@Transactional
//	void getAllRoles() throws Exception {
//		restUserMockMvc
//				.perform(get("/api/v1.0/roles").accept(MediaType.APPLICATION_JSON)
//						.contentType(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//				.andExpect(jsonPath("$").isArray())
//				.andExpect(jsonPath("$").value(hasItems(RolesConstants.USER, RolesConstants.ADMIN)));
//	}
//
//	@Test
//	@Transactional
//	void getAllUsersSortedByParameters() throws Exception {
//		// Initialize the database
//		userRepository.saveAndFlush(user);
//
//		restUserMockMvc.perform(get("/api/v1.0/users?sort=resetKey,desc").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest());
//		restUserMockMvc.perform(get("/api/v1.0/users?sort=password,desc").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest());
//		restUserMockMvc
//				.perform(get("/api/v1.0/users?sort=resetKey,desc&sort=id,desc").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isBadRequest());
//		restUserMockMvc.perform(get("/api/v1.0/users?sort=id,desc").accept(MediaType.APPLICATION_JSON))
//				.andExpect(status().isOk());
//	}
//}
