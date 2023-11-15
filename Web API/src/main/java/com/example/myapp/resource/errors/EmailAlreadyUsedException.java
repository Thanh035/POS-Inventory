package com.example.myapp.resource.errors;

public class EmailAlreadyUsedException extends BadRequestAlertException {

	private static final long serialVersionUID = 1L;

	public EmailAlreadyUsedException() {
		super("Email is already in use!");
	}
}
