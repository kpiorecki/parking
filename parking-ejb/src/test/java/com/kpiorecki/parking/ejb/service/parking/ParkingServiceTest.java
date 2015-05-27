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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.dto.AddressDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.dto.RecordDto;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class ParkingServiceTest {

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
	private Mapper mapper;

	@Inject
	private TestUtilities testUtilities;

	private Parking parking;
	private String parkingUuid;
	private LocalDate bookingDate;

	private String addedLogin1 = "user1";
	private String addedLogin2 = "user2";
	private String freeLogin3 = "user3";

	@Before
	public void prepareData() {
		User addedUser1 = testUtilities.persistUser(addedLogin1);
		User addedUser2 = testUtilities.persistUser(addedLogin2);

		parking = testUtilities.persistParking(addedUser1, addedUser2);
		parkingUuid = parking.getUuid();
		bookingDate = new LocalDate(2015, 04, 01);

		testUtilities.persistUser(freeLogin3);
		testUtilities.persistBooking(parking, bookingDate, addedUser1, addedUser2);

		entityManager.flush();
	}

	@Test
	public void shouldAddParking() {
		// given
		AddressDto addressDto = new AddressDto();
		addressDto.setCity("city");
		addressDto.setPostalCode("code");
		addressDto.setStreet("street");
		addressDto.setNumber("number");

		ParkingDto parkingDto = new ParkingDto();
		parkingDto.setAddress(addressDto);
		parkingDto.setCapacity(50);
		parkingDto.setName("name");

		// when
		String uuid = parkingService.addParking(parkingDto);

		// then
		ParkingDto foundParking = parkingService.findParking(uuid);
		assertNotNull(foundParking);
	}

	@Test
	public void shouldModifyParking() {
		// given
		ParkingDto parkingDto = mapper.map(parking, ParkingDto.class);

		// when
		parkingDto.setCapacity(100);
		parkingDto.getAddress().setCity("new city");
		parkingService.modifyParking(parkingDto);

		// then
		ParkingDto foundParkingDto = parkingService.findParking(parkingUuid);
		assertNotNull(foundParkingDto);
		assertEquals((Integer) 100, foundParkingDto.getCapacity());
		assertEquals("new city", foundParkingDto.getAddress().getCity());
	}

	@Test
	public void shouldDeleteParking() {
		// when
		parkingService.deleteParking(parkingUuid);

		// then
		List<ParkingDto> allParkings = parkingService.findAllParkings();
		assertTrue(allParkings.isEmpty());

		Booking booking = bookingDao.find(parkingUuid, bookingDate);
		assertNull(booking);
	}

	@Test(expected = Exception.class)
	public void shouldNotFindDeletedParking() {
		// when
		parkingService.deleteParking(parkingUuid);
		parkingService.findParking(parkingUuid);

		// then exception should be thrown
	}

	@Test
	public void shouldFindParking() {
		// when
		ParkingDto parkingDto = parkingService.findParking(parkingUuid);

		// then
		assertNotNull(parkingDto);
		assertEquals(parkingUuid, parkingDto.getUuid());
	}

	@Test
	public void shouldFindAllParkings() {
		// when
		List<ParkingDto> allParkings = parkingService.findAllParkings();

		// then
		assertNotNull(allParkings);
		assertEquals(1, allParkings.size());
	}

	@Test
	public void shouldFindRecords() {
		// when
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);

		// then
		assertNotNull(records);
		assertEquals(2, records.size());
	}

	@Test
	public void shouldAssignUser() {
		// when
		parkingService.assignUser(parkingUuid, freeLogin3, true);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(3, records.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotAssignUserTwice() {
		// when
		parkingService.assignUser(parkingUuid, addedLogin1, true);
		entityManager.flush();

		// then unique constraint violation should be thrown (user already assigned)
	}

	@Test
	public void shouldRevokeUser() {
		// when
		parkingService.revokeUser(parkingUuid, addedLogin1);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(1, records.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotRevokeUnassignedUser() {
		// when
		parkingService.revokeUser(parkingUuid, freeLogin3);

		// then exception should be thrown - user was not assigned to parking
	}

	@Test
	public void shouldRevokeAllUsers() {
		// when
		parkingService.revokeAllUsers(parkingUuid);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertTrue(records.isEmpty());
	}

}
