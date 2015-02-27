package com.kpiorecki.parking.core;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

import com.kpiorecki.parking.core.entity.User;

public class UserPersistenceTest extends IntegrationTest {

	@Inject
	private EntityManager entityManager;

	@Test
	public void shouldPersistUser() throws Exception {
		User user = new User();
		user.setFirstName("firstname");
		user.setLastName("lastname");

		entityManager.persist(user);
		assertNotNull(user.getId());
	}

}
