package com.example.myapp.resource.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionTranslator {

	@ExceptionHandler(BadRequestAlertException.class)
	public ResponseEntity<Object> handleBadRequestAlertException(BadRequestAlertException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LoginAlreadyUsedException.class)
	public ResponseEntity<Object> handleLoginAlreadyUsedException(LoginAlreadyUsedException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EmailAlreadyUsedException.class)
	public ResponseEntity<Object> handleEmailAlreadyException(EmailAlreadyUsedException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<Object> handleInvalidPasswordException(InvalidPasswordException ex) {
		return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

}