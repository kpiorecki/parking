package com.kpiorecki.parking.core;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.junit.Test;

import com.kpiorecki.parking.core.entity.User;

public class UserPersistenceTest extends IntegrationTest {

	@Inject
	private EntityManager entityManager;

	@Inject
	private UserTransaction userTransaction;

	@Test
	public void shouldPersistUser() throws Exception {
		userTransaction.begin();
		entityManager.joinTransaction();

		User user = new User();
		user.setFirstName("firstname");
		user.setLastName("lastname");

		entityManager.persist(user);
		assertNotNull(user.getId());

		userTransaction.commit();
	}

}
