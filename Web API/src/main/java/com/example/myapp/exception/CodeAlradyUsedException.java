package com.example.myapp.exception;

public class CodeAlradyUsedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CodeAlradyUsedException() {
		super("Code is already in use!");
	}
	
}
