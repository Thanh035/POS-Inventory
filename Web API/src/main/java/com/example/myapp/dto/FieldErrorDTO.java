package com.example.myapp.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldErrorDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String objectName;

	private final String field;

	private final String message;
}
