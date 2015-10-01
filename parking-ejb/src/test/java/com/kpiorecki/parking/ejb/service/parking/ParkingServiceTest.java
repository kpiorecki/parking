package com.kpiorecki.parking.ejb.service.parking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.GlassFishSecuredTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.dao.HolidayScheduleDao;
import com.kpiorecki.parking.ejb.dto.AddressDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.dto.RecordDto;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class ParkingServiceTest extends GlassFishSecuredTest {

	// TODO add holiday schedules handling to tests

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createFullDeployment();
	}

	@Inject
	private ParkingService parkingService;

	@Inject
	private EntityManager entityManager;

	@Inject
	private BookingDao bookingDao;

	@Inject
	private HolidayScheduleDao scheduleDao;

	@Inject
	private Mapper mapper;

	@Inject
	private TestUtilities testUtilities;

	@Test
	public void shouldAddParking() {
		// when
		AddressDto addressDto = new AddressDto();
		addressDto.setCity("city");
		addressDto.setPostalCode("code");
		addressDto.setStreet("street");
		addressDto.setNumber("number");

		ParkingDto parkingDto = new ParkingDto();
		parkingDto.setAddress(addressDto);
		parkingDto.setCapacity(50);
		parkingDto.setName("name");

		String uuid = parkingService.addParking(parkingDto);

		// then
		ParkingDto foundParking = parkingService.findParking(uuid);
		assertNotNull(foundParking);
	}

	@Test
	public void shouldModifyParking() {
		// given
		Parking parking = testUtilities.persistParking();
		entityManager.flush();

		// when
		ParkingDto parkingDto = mapper.map(parking, ParkingDto.class);
		parkingDto.setCapacity(100);
		parkingDto.getAddress().setCity("new city");
		parkingService.modifyParking(parkingDto);

		// then
		ParkingDto foundParkingDto = parkingService.findParking(parking.getUuid());
		assertNotNull(foundParkingDto);
		assertEquals((Integer) 100, foundParkingDto.getCapacity());
		assertEquals("new city", foundParkingDto.getAddress().getCity());
	}

	@Test
	public void shouldDeleteParking() {
		// given
		User user = testUtilities.persistUser("login");
		Parking parking = testUtilities.persistParking(user);
		LocalDate bookingDate = new LocalDate(2015, 04, 01);
		testUtilities.persistBooking(parking, bookingDate, user);

		HolidaySchedule schedule = testUtilities.createSchedule();
		schedule.addParking(parking);
		entityManager.persist(schedule);

		entityManager.flush();

		// when
		parkingService.deleteParking(parking.getUuid());

		// then
		List<ParkingDto> allParkings = parkingService.findAllParkings();
		assertTrue(allParkings.isEmpty());

		Booking booking = bookingDao.find(parking.getUuid(), bookingDate);
		assertNull(booking);

		HolidaySchedule foundSchedule = scheduleDao.find(schedule.getUuid());
		assertTrue(foundSchedule.getParkings().isEmpty());
	}

	@Test
	public void shouldNotFindDeletedParking() {
		// given
		String parkingUuid = testUtilities.persistParking().getUuid();
		entityManager.flush();

		// when
		parkingService.deleteParking(parkingUuid);
		ParkingDto foundParking = parkingService.findParking(parkingUuid);

		// then
		assertNull(foundParking);
	}

	@Test
	public void shouldFindParking() {
		// given
		String parkingUuid = testUtilities.persistParking().getUuid();
		entityManager.flush();

		// when
		ParkingDto parkingDto = parkingService.findParking(parkingUuid);

		// then
		assertNotNull(parkingDto);
		assertEquals(parkingUuid, parkingDto.getUuid());
	}

	@Test
	public void shouldFindAllParkings() {
		// given
		testUtilities.persistParking();
		testUtilities.persistParking();
		testUtilities.persistParking();
		entityManager.flush();

		// when
		List<ParkingDto> allParkings = parkingService.findAllParkings();

		// then
		assertNotNull(allParkings);
		assertEquals(3, allParkings.size());
	}

	@Test
	public void shouldFindRecords() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		entityManager.flush();

		// when
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);

		// then
		assertNotNull(records);
		assertEquals(2, records.size());
	}

	@Test
	public void shouldAssignUser() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		testUtilities.persistUser("login3");
		entityManager.flush();

		// when
		parkingService.assignUser(parkingUuid, "login3", true);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(3, records.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotAssignUserTwice() {
		// given
		User user = testUtilities.persistUser("login");
		String parkingUuid = testUtilities.persistParking(user).getUuid();
		entityManager.flush();

		// when
		parkingService.assignUser(parkingUuid, "login", true);
		entityManager.flush();

		// then unique constraint violation should be thrown (user already assigned)
	}

	@Test
	public void shouldRevokeUser() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		entityManager.flush();

		// when
		parkingService.revokeUser(parkingUuid, "login1");

		// then
		List<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(1, records.size());
		assertEquals("login2", records.get(0).getUser().getLogin());
	}

	@Test(expected = Exception.class)
	public void shouldNotRevokeUnassignedUser() {
		// given
		User user = testUtilities.persistUser("login");
		String parkingUuid = testUtilities.persistParking(user).getUuid();
		entityManager.flush();

		// when
		parkingService.revokeUser(parkingUuid, "new login");

		// then exception should be thrown - user was not assigned to parking
	}

	@Test
	public void shouldRevokeAllUsers() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		entityManager.flush();

		// when
		parkingService.revokeAllUsers(parkingUuid);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertTrue(records.isEmpty());
	}

	@Test
	public void shouldNotFindUserParkings() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		List<ParkingDto> userParkings = parkingService.findUserParkings("login");

		// then
		assertNotNull(userParkings);
		assertTrue(userParkings.isEmpty());
	}

	@Test
	public void shouldFindUserParkings() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		User user3 = testUtilities.persistUser("login3");
		String parking1Uuid = testUtilities.persistParking(user1, user2).getUuid();
		String parking2Uuid = testUtilities.persistParking(user1, user2, user3).getUuid();
		String parking3Uuid = testUtilities.persistParking(user1).getUuid();
		String parking4Uuid = testUtilities.persistParking(user2, user3).getUuid();
		entityManager.flush();

		// when
		List<ParkingDto> userParkings = parkingService.findUserParkings("login1");

		// then
		assertNotNull(userParkings);
		assertEquals(3, userParkings.size());
		assertTrue(userParkings.stream().anyMatch(u -> u.getUuid().equals(parking1Uuid)));
		assertTrue(userParkings.stream().anyMatch(u -> u.getUuid().equals(parking2Uuid)));
		assertTrue(userParkings.stream().anyMatch(u -> u.getUuid().equals(parking3Uuid)));
		assertTrue(userParkings.stream().noneMatch(u -> u.getUuid().equals(parking4Uuid)));
	}
}
