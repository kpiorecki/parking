package com.kpiorecki.parking.ejb.service.user.impl;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.util.ResourceProducer;

@RunWith(Arquillian.class)
public class UserPasswordEncoderTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBaseDeployment().addClasses(ResourceProducer.class, UserPasswordEncoder.class);
	}

	@Inject
	private UserPasswordEncoder encoder;

	@Test
	public void shouldEncodeTexts() {
		shouldEncodeText("admin", "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
		shouldEncodeText("user", "04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb");
	}

	private void shouldEncodeText(String plainText, String expectedEncodedText) {
		// when
		String encodedText = encoder.encode(plainText);

		// then
		assertEquals(UserPasswordEncoder.PASSWORD_LENGTH, encodedText.length());
		assertEquals(expectedEncodedText, encodedText);
	}
}
