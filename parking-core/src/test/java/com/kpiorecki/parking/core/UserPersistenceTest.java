package com.kpiorecki.parking.core;

import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.junit.Test;

import com.kpiorecki.parking.core.entity.User;
import com.kpiorecki.parking.core.entity.Vehicle;

public class UserPersistenceTest extends IntegrationTest {

	@Inject
	private EntityManager entityManager;

	@Inject
	private UserTransaction userTransaction;

	@Test
	public void shouldPersistUser() throws Exception {
		userTransaction.begin();
		entityManager.joinTransaction();

		Set<Vehicle> vehicles = new HashSet<>();
		Vehicle vehicle1 = new Vehicle();
		vehicle1.setNumber("number1");
		vehicles.add(vehicle1);
		Vehicle vehicle2 = new Vehicle();
		vehicle2.setNumber("number2");
		vehicles.add(vehicle2);

		User user = new User();
		user.setLogin("login");
		user.setActive(true);
		user.setEmail("user@user.com");
		user.setVehicles(vehicles);

		entityManager.persist(user);
		assertNotNull(user.getId());

		userTransaction.commit();
	}

}
