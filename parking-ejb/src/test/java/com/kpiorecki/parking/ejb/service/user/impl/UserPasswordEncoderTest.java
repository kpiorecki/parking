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
	public void shouldEncodeText() {
		// given
		String plainText = "admin";

		// when
		String encodedText = encoder.encode(plainText);

		// then
		assertEquals(64, encodedText.length());
		assertEquals("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", encodedText);
	}
}
