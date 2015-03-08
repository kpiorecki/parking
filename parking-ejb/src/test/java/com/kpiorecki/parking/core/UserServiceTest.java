package com.kpiorecki.parking.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.kpiorecki.parking.core.dto.UserDto;
import com.kpiorecki.parking.core.entity.User;
import com.kpiorecki.parking.core.service.UserService;

public class UserServiceTest extends IntegrationTest {

	@Inject
	private UserService userService;

	@Inject
	private EntityManager entityManager;

	private String login = "login";

	@Before
	public void prepareData() {
		persistUser(login);
		entityManager.flush();
	}

	@Test
	public void shouldAddUser() {
		// given
		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("new login");
		user.setEmail("user@mail.com");

		// when
		userService.addUser(user);

		// then
		UserDto foundUser = userService.findUser("new login");
		assertNotNull(foundUser);
	}

	@Test
	public void shouldModifyUser() {
		// given
		UserDto user = userService.findUser(login);

		// when
		user.setFirstName("new firstname");
		userService.modifyUser(user);

		// then
		UserDto foundUser = userService.findUser(login);
		assertNotNull(foundUser);
		assertEquals("new firstname", foundUser.getFirstName());
	}

	@Test
	public void shouldNotFindUser() {
		// when
		UserDto user = userService.findUser("new login");

		// then
		assertNull(user);
	}

	@Test(expected = Exception.class)
	public void shouldDuplicateLogin() {
		// when
		UserDto userDto = new UserDto();
		userDto.setLogin(login);
		userDto.setEmail("user@mail.com");

		userService.addUser(userDto);
		entityManager.flush();

		// then exception should be thrown
	}

	@Test
	public void shouldDeleteUser() {
		// when
		userService.deleteUser(login);

		// then
		UserDto user = userService.findUser(login);
		assertNull(user);
	}

	@Test(expected = Exception.class)
	public void shouldNotDeleteUser() {
		// when
		userService.deleteUser("new login");

		// then DomainException should be thrown
	}

	private void persistUser(String login) {
		User user = new User();
		user.setLogin(login);
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setEmail(login + "@mail.com");

		entityManager.persist(user);
	}
}
