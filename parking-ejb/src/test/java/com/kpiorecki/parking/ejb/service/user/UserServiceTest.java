package com.kpiorecki.parking.ejb.service.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.GreenMailTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.User_;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class UserServiceTest extends GreenMailTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createFullDeployment();
	}

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

	@Test
	public void shouldAddUser() {
		// when
		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("new login");
		user.setEmail("user@mail.com");
		user.setPassword("password");
		userService.addUser(user);

		// then
		UserDto foundUser = userService.findUser("new login");
		assertNotNull(foundUser);
	}

	@Test
	public void shouldModifyUser() {
		// given
		User user = testUtilities.persistUser("login");
		entityManager.flush();

		// when
		UserDto userDto = mapper.map(user, UserDto.class);
		userDto.setFirstName("new firstname");
		userService.modifyUser(userDto);

		// then
		UserDto foundUser = userService.findUser("login");
		assertNotNull(foundUser);
		assertEquals("new firstname", foundUser.getFirstName());
	}

	@Test
	public void shouldFindUser() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		UserDto user = userService.findUser("login");

		// then
		assertNotNull(user);
	}

	@Test
	public void shouldNotFindNonExistingUser() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		UserDto foundUser = userService.findUser("new login");

		// then
		assertNull(foundUser);
	}

	@Test
	public void shouldFindAllUsers() {
		// given
		testUtilities.persistUser("login1");
		testUtilities.persistUser("login2");

		// when
		List<UserDto> allUsers = userService.findAllUsers();

		// then
		assertNotNull(allUsers);
		assertEquals(2, allUsers.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotAddDuplicatedUserLogin() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		UserDto userDto = new UserDto();
		userDto.setLogin("login");
		userDto.setEmail("user@mail.com");
		userDto.setPassword("password");
		userService.addUser(userDto);
		entityManager.flush();

		// then exception should be thrown
	}

	@Test
	public void shouldDeleteUser() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		entityManager.flush();

		// when
		userService.deleteUser("login1");

		// then
		List<User> foundUsers = userDao.find(User_.login, "login1");
		assertTrue(foundUsers.isEmpty());

		boolean user1Assigned = parkingDao.isUserAssigned(parkingUuid, "login1");
		assertFalse(user1Assigned);

		boolean user2Assigned = parkingDao.isUserAssigned(parkingUuid, "login2");
		assertTrue(user2Assigned);
	}

	@Test(expected = Exception.class)
	public void shouldNotDeleteNonExistingUser() {
		// when
		userService.deleteUser("new login");

		// then DomainException should be thrown
	}

	@Test
	public void shouldFindLoginAvailable() {
		// when
		boolean loginAvailable = userService.isLoginAvailable("availableLogin");

		// then
		assertTrue(loginAvailable);
	}

	@Test
	public void shouldFindLoginNotAvailable() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		boolean loginAvailable = userService.isLoginAvailable("login");

		// then
		assertFalse(loginAvailable);
	}

	@Test
	public void shouldRegisterUser() {
		// given
		String activationUuid = "testActivationUuid";
		String activationURL = "http://localhost:8080/activation";

		UserDto user = new UserDto();
		user.setLogin("login");
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setEmail("user@mail.com");
		user.setPassword("password");

		// when
		userService.registerUser(user, activationUuid, activationURL);

		// then
		assertOneMailSent();
		assertNull(userService.findUser("login"));
		assertFalse(userService.isLoginAvailable("login"));
	}

	@Test
	public void shouldActivateUser() {
		// given
		String activationUuid = "uuid";
		String login = "activationLogin";
		User user = testUtilities.createUser(login);
		user.setActivationUuid(activationUuid);
		user.setActivationDeadline(new DateTime().plusDays(1));
		entityManager.persist(user);

		// when
		UserDto activatedUser = userService.activateUser(activationUuid);

		// then
		assertNotNull(activatedUser);
		assertEquals(login, activatedUser.getLogin());
	}

	@Test
	public void shouldNotActivateUser() {
		// when
		UserDto activatedUser = userService.activateUser("nonExistingUuid");

		// then
		assertNull(activatedUser);
	}
}
