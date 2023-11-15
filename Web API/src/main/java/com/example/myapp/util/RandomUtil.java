package com.example.myapp.util;

import java.security.SecureRandom;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomUtil {

	private RandomUtil() {
		
	}
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public static String generatePassword() {
		return generateRandomAlphanumericString();
	}

	public static String generateRandomAlphanumericString() {
		return RandomStringUtils.random(20, 0, 0, true, true, (char[]) null, SECURE_RANDOM);
	}

	public static String generateResetKey() {
		return generateRandomAlphanumericString();
	}

	public static String generateActivationKey() {
		return generateRandomAlphanumericString();
	}

	static {
		SECURE_RANDOM.nextBytes(new byte[64]);
	}
}
