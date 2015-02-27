package com.kpiorecki.parking.core;

import javax.inject.Inject;

import org.junit.Test;

import com.kpiorecki.parking.core.dto.UserDto;
import com.kpiorecki.parking.core.service.UserService;

public class UserServiceTest extends IntegrationTest {

	@Inject
	private UserService userService;

	@Test
	public void shouldSaveUser() {
		UserDto userDto = new UserDto();
		userDto.setFirstName("firstname");
		userDto.setLastName("lastname");

		userService.saveUser(userDto);
	}
}
