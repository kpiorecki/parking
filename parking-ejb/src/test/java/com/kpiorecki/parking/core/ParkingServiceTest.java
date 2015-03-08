package com.kpiorecki.parking.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import com.kpiorecki.parking.core.dto.AddressDto;
import com.kpiorecki.parking.core.dto.ParkingDto;
import com.kpiorecki.parking.core.dto.RecordDto;
import com.kpiorecki.parking.core.entity.Address;
import com.kpiorecki.parking.core.entity.Parking;
import com.kpiorecki.parking.core.entity.Record;
import com.kpiorecki.parking.core.entity.User;
import com.kpiorecki.parking.core.service.ParkingService;
import com.kpiorecki.parking.core.service.impl.UuidGenerator;

public class ParkingServiceTest extends IntegrationTest {

	@Inject
	private ParkingService parkingService;

	@Inject
	private EntityManager entityManager;

	@Inject
	private UuidGenerator uuidGenerator;

	private String parkingUuid;
	private String addedUserLogin1 = "user1";
	private String addedUserLogin2 = "user2";
	private String freeUserLogin3 = "user3";

	@Before
	public void prepareData() {
		parkingUuid = uuidGenerator.generateUuid();

		User addedUser1 = persistUser(addedUserLogin1);
		User addedUser2 = persistUser(addedUserLogin2);
		persistUser(freeUserLogin3);

		persistParking(parkingUuid, addedUser1, addedUser2);

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
		ParkingDto parking = parkingService.findParking(parkingUuid);

		// when
		parking.setCapacity(100);
		parking.getAddress().setCity("new city");
		parkingService.modifyParking(parking);

		// then
		ParkingDto foundParking = parkingService.findParking(parkingUuid);
		assertNotNull(foundParking);
		assertEquals((Integer) 100, foundParking.getCapacity());
		assertEquals("new city", foundParking.getAddress().getCity());
	}

	@Test
	public void shouldDeleteParking() {
		// when
		parkingService.deleteParking(parkingUuid);

		// then
		ParkingDto parking = parkingService.findParking(parkingUuid);
		assertNull(parking);
	}

	@Test
	public void shouldFindParking() {
		// when
		ParkingDto parking = parkingService.findParking(parkingUuid);

		// then
		assertNotNull(parking);
		assertEquals(parkingUuid, parking.getUuid());
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
		parkingService.assignUser(parkingUuid, freeUserLogin3, true);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(3, records.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotAssignUser() {
		// when
		parkingService.assignUser(parkingUuid, addedUserLogin1, true);
		entityManager.flush();

		// then unique constraint violation should be thrown (user already assigned)
	}

	@Test
	public void shouldRevokeUser() {
		// when
		parkingService.revokeUser(parkingUuid, addedUserLogin1);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(1, records.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotRevokeUser() {
		// when
		parkingService.revokeUser(parkingUuid, freeUserLogin3);

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

	private User persistUser(String login) {
		User user = new User();
		user.setLogin(login);
		user.setEmail(login + "@mail.com");

		entityManager.persist(user);

		return user;
	}

	private Parking persistParking(String parkingUuid, User... users) {
		Address address = new Address();
		address.setCity("city");
		address.setPostalCode("code");
		address.setStreet("street");
		address.setNumber("number");

		Set<Record> records = new HashSet<>();
		for (User user : users) {
			Record record = new Record();
			record.setUser(user);
			record.setVip(false);
			record.setPoints(0);

			records.add(record);
		}

		Parking parking = new Parking();
		parking.setUuid(parkingUuid);
		parking.setAddress(address);
		parking.setCapacity(50);
		parking.setName("name");
		parking.setRecords(records);

		entityManager.persist(parking);

		return parking;
	}
}
