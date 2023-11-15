package com.example.myapp.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordChangeDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String currentPassword;
	
	private String newPassword;
}
