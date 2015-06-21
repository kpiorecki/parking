package com.kpiorecki.parking.ejb.service.user.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

@Stateless
public class UserPasswordEncoder {

	public static final int PASSWORD_LENGTH = 64;

	@Inject
	private Logger logger;

	public String encode(String plainText) {
		byte[] digest = encodeSHA256(plainText);
		String hexString = encodeHex(digest);

		logger.debug("encoded password to SHA-256 Hex={}", hexString);

		return hexString;
	}

	private byte[] encodeSHA256(String plainText) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(plainText.getBytes("UTF-8"));
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			String message = "could not encode text using SHA-256 algorithm";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

	private String encodeHex(byte[] digest) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < digest.length; ++i) {
			String hex = Integer.toHexString(0xff & digest[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
