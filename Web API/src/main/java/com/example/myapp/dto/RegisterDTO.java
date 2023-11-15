package com.example.myapp.dto;

import javax.validation.constraints.Size;

import com.example.myapp.dto.UserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RegisterDTO extends UserDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int PASSWORD_MIN_LENGTH = 4;

	public static final int PASSWORD_MAX_LENGTH = 100;

	@Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
	private String password;

}
