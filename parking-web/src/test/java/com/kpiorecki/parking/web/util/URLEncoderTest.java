package com.kpiorecki.parking.web.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class URLEncoderTest {

	private URLEncoder encoder = new URLEncoder();

	@Test
	public void shouldEncodeAndDecode() {
		String nastyParameter = "nasty_parameter:/?#[]@!$&'()*+,;=ąężźć";
		String encodedText = encoder.encode(nastyParameter);
		String decodedText = encoder.decode(encodedText);

		assertEquals(nastyParameter, decodedText);
	}
}
