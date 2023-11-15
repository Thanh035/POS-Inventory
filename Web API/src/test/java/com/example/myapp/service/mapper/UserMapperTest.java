package com.example.myapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;
import com.example.myapp.mapper.UserMapper;

class UserMapperTest {

	private static final String DEFAULT_LOGIN = "thanh";
	private static final Long DEFAULT_ID = 1L;

	private UserMapper userMapper;
	private User user;
	private UserDTO userDTO;

	@BeforeEach
	public void init() {
		userMapper = new UserMapper();
		user = new User();
		user.setLogin(DEFAULT_LOGIN);
		user.setPassword(RandomStringUtils.randomAlphanumeric(60));
		user.setActivated(true);
		user.setEmail("thanh@localhost");
		user.setFullname("thanh");
		user.setImageUrl("image_url");

		userDTO = new UserDTO(user);
	}

	@Test
	void usersToUserDTOsShouldMapOnlyNonNullUsers() {
		List<User> users = new ArrayList<>();
		users.add(user);
		users.add(null);

		List<UserDTO> userDTOs = userMapper.usersToUserDTOs(users);

		assertThat(userDTOs).isNotEmpty().size().isEqualTo(1);
	}

	@Test
	void userDTOsToUsersShouldMapOnlyNonNullUsers() {
		List<UserDTO> usersDTO = new ArrayList<>();
		usersDTO.add(userDTO);
		usersDTO.add(null);

		List<User> users = userMapper.userDTOsToUsers(usersDTO);

		assertThat(users).isNotEmpty().size().isEqualTo(1);
	}

	@Test
	void userDTOsToUsersWithAuthoritiesStringShouldMapToUsersWithAuthoritiesDomain() {
		List<String> rolesAsString = new ArrayList<>();
		rolesAsString.add("ROLE_ADMIN");
		userDTO.setRoles(rolesAsString);

		List<UserDTO> usersDTO = new ArrayList<>();
		usersDTO.add(userDTO);

		List<User> users = userMapper.userDTOsToUsers(usersDTO);

		assertThat(users).isNotEmpty().size().isEqualTo(1);
		assertThat(users.get(0).getRoles()).isNotNull();
		assertThat(users.get(0).getRoles()).isNotEmpty();
		assertThat(users.get(0).getRoles().iterator().next().getCode()).isEqualTo("ROLE_ADMIN");
	}

	@Test
	void userDTOsToUsersMapWithNullRolesStringShouldReturnUserWithEmptyRoles() {
		userDTO.setRoles(null);

		List<UserDTO> usersDTO = new ArrayList<>();
		usersDTO.add(userDTO);

		List<User> users = userMapper.userDTOsToUsers(usersDTO);

		assertThat(users).isNotEmpty().size().isEqualTo(1);
		assertThat(users.get(0).getRoles()).isNotNull();
		assertThat(users.get(0).getRoles()).isEmpty();
	}

	@Test
	void userDTOToUserMapWithRolesStringShouldReturnUserWithRoles() {
		List<String> rolesAsString = new ArrayList<>();
		rolesAsString.add("ROLE_ADMIN");
		userDTO.setRoles(rolesAsString);

		User user = userMapper.userDTOToUser(userDTO);

		assertThat(user).isNotNull();
		assertThat(user.getRoles()).isNotNull();
		assertThat(user.getRoles()).isNotEmpty();
		assertThat(user.getRoles().iterator().next().getCode()).isEqualTo("ROLE_ADMIN");
	}

	@Test
	void userDTOToUserMapWithNullRolesStringShouldReturnUserWithEmptyRoles() {
		userDTO.setRoles(null);

		User user = userMapper.userDTOToUser(userDTO);

		assertThat(user).isNotNull();
		assertThat(user.getRoles()).isNotNull();
		assertThat(user.getRoles()).isEmpty();
	}

	@Test
	void userDTOToUserMapWithNullUserShouldReturnNull() {
		assertThat(userMapper.userDTOToUser(null)).isNull();
	}

	@Test
	void testUserFromId() {
		assertThat(userMapper.userFromId(DEFAULT_ID).getId()).isEqualTo(DEFAULT_ID);
		assertThat(userMapper.userFromId(null)).isNull();
	}
}
