package com.kpiorecki.parking.ejb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;

import com.kpiorecki.parking.ejb.IntegrationTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.User_;

public class UserServiceTest extends IntegrationTest {

	@Inject
	private UserService userService;

	@Inject
	private EntityManager entityManager;

	@Inject
	private UserDao userDao;

	@Inject
	private ParkingDao parkingDao;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	private Mapper mapper;

	private String login1 = "login1";
	private String login2 = "login2";

	private String parkingUuid;

	private User user1;

	@Before
	public void prepareData() {
		user1 = testUtilities.persistUser(login1);
		User user2 = testUtilities.persistUser(login2);
		parkingUuid = testUtilities.persistParking(user1, user2).getUuid();

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
		UserDto user = mapper.map(user1, UserDto.class);

		// when
		user.setFirstName("new firstname");
		userService.modifyUser(user);

		// then
		UserDto foundUser = userService.findUser(login1);
		assertNotNull(foundUser);
		assertEquals("new firstname", foundUser.getFirstName());
	}

	@Test
	public void shouldFindUser() {
		// when
		UserDto user = userService.findUser(login1);

		// then
		assertNotNull(user);
	}

	@Test(expected = Exception.class)
	public void shouldNotFindNonExistingUser() {
		// when
		userService.findUser("new login");

		// then exception should be thrown
	}

	@Test
	public void shouldFindAllUsers() {
		// when
		List<UserDto> allUsers = userService.findAllUsers();

		// then
		assertNotNull(allUsers);
		assertEquals(2, allUsers.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotAddDuplicatedUserLogin() {
		// when
		UserDto userDto = new UserDto();
		userDto.setLogin(login1);
		userDto.setEmail("user@mail.com");

		userService.addUser(userDto);
		entityManager.flush();

		// then exception should be thrown
	}

	@Test
	public void shouldDeleteUser() {
		// when
		userService.deleteUser(login1);

		// then
		List<User> foundUsers = userDao.find(User_.login, login1);
		assertTrue(foundUsers.isEmpty());

		boolean user1Assigned = parkingDao.isUserAssigned(parkingUuid, login1);
		assertFalse(user1Assigned);

		boolean user2Assigned = parkingDao.isUserAssigned(parkingUuid, login2);
		assertTrue(user2Assigned);
	}

	@Test(expected = Exception.class)
	public void shouldNotDeleteNonExistingUser() {
		// when
		userService.deleteUser("new login");

		// then DomainException should be thrown
	}

}
