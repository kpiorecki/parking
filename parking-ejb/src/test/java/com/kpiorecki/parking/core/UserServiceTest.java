package com.kpiorecki.parking.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

import com.kpiorecki.parking.core.dto.UserDto;
import com.kpiorecki.parking.core.service.UserService;

public class UserServiceTest extends IntegrationTest {

	@Inject
	private UserService userService;

	@Inject
	private EntityManager entityManager;

	private final String defaultLogin = "login";

	@Test
	public void shouldAddUser() {
		// given
		UserDto user = createDefaultUser();

		// when
		userService.addUser(user);

		// then
		UserDto foundUser = userService.findUser(defaultLogin);
		assertNotNull(foundUser);
	}

	@Test
	public void shouldModifyUser() {
		// given
		userService.addUser(createDefaultUser());
		UserDto user = userService.findUser(defaultLogin);

		// when
		user.setFirstName("new firstname");
		userService.modifyUser(user);

		// then
		UserDto foundUser = userService.findUser(defaultLogin);
		assertNotNull(foundUser);
		assertEquals("new firstname", foundUser.getFirstName());
	}

	@Test
	public void shouldNotFindUser() {
		// when
		UserDto user = userService.findUser(defaultLogin);

		// then
		assertNull(user);
	}

	@Test(expected = Exception.class)
	public void shouldThrowExceptionWithDuplicatedLogin() {
		// given
		userService.addUser(createDefaultUser());

		// when
		userService.addUser(createDefaultUser());
		entityManager.flush();

		// then exception should be thrown
	}

	private UserDto createDefaultUser() {
		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin(defaultLogin);

		return user;
	}
}
