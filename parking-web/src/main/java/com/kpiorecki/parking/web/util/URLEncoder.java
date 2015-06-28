package com.kpiorecki.parking.web.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

@Stateless
public class URLEncoder {

	@Inject
	private Logger logger;

	public String encode(String plainText) {
		byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
		return Base64.getUrlEncoder().encodeToString(bytes);
	}

	public String decode(String encodedText) {
		try {
			byte[] bytes = Base64.getUrlDecoder().decode(encodedText);
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			logger.warn("could not decode value {} - invalid base64 scheme - returning null", encodedText);
			return null;
		}
	}

}
