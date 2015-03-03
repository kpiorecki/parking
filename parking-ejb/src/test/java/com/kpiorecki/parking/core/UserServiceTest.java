package com.kpiorecki.parking.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Test;

import com.kpiorecki.parking.core.dto.UserDto;
import com.kpiorecki.parking.core.service.UserService;

public class UserServiceTest extends IntegrationTest {

	@Inject
	private UserService userService;

	@Test
	public void shouldAddUser() {
		// given
		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("login");

		// when
		userService.addUser(user);

		// then
		UserDto foundUser = userService.findUser("login");
		assertNotNull(foundUser);
	}

	@Test
	public void shouldModifyUser() {
		// given
		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("login");

		userService.addUser(user);
		user = userService.findUser("login");

		// when
		user.setFirstName("new firstname");
		userService.modifyUser(user);

		// then
		UserDto foundUser = userService.findUser("login");
		assertNotNull(foundUser);
		assertEquals("new firstname", foundUser.getFirstName());
	}
}
