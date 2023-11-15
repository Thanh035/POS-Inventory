package com.example.myapp.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.example.myapp.domain.Role;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;

@Service
public class UserMapper {

//	@Autowired
//	private ModelMapper mapper;

	public List<UserDTO> usersToUserDTOs(List<User> users) {
		return users.stream().filter(Objects::nonNull).map(this::userToUserDTO).collect(Collectors.toList());
	}

	public UserDTO userToUserDTO(User user) {
		return new UserDTO(user);
	}

	private List<Role> rolesFromStrings(List<String> rolesAsString) {
		List<Role> roles = new ArrayList<>();

		if (rolesAsString != null) {
			roles = rolesAsString.stream().map(string -> {
				Role role = new Role();
				role.setCode(string);
				return role;
			}).collect(Collectors.toList());
		}
		return roles;
	}

	public User userFromId(Long id) {
		if (id == null) {
			return null;
		}
		User user = new User();
		user.setId(id);
		return user;
	}

	public User userDTOToUser(UserDTO userDTO) {
		if (userDTO == null) {
			return null;
		} else {
			User user = new User();

//			user.setId(userDTO.getId());
//			user.setLogin(userDTO.getLogin());
//			user.setFullname(userDTO.getFullname());
//			user.setEmail(userDTO.getEmail());
//			user.setImageUrl(userDTO.getImageUrl());
//			user.setActivated(userDTO.isActivated());

			BeanUtils.copyProperties(userDTO,user);

			List<Role> roles = this.rolesFromStrings(userDTO.getRoles());
			user.setRoles(roles);
			return user;
		}
	}

	public List<User> userDTOsToUsers(List<UserDTO> userDTOs) {

		return userDTOs.stream()
				.filter(Objects::nonNull)
				.map(this::userDTOToUser)
				.collect(Collectors.toList());
	}

	public UserDTO toDtoId(User user) {
		if (user == null) {
			return null;
		}
		UserDTO userDto = new UserDTO();
		userDto.setId(user.getId());
		return userDto;
	}

}
