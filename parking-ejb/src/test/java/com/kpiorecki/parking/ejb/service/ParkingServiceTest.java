package com.kpiorecki.parking.ejb.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;

import com.kpiorecki.parking.ejb.IntegrationTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dto.AddressDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.dto.RecordDto;
import com.kpiorecki.parking.ejb.entity.Parking;

public class ParkingServiceTest extends IntegrationTest {

	@Inject
	private ParkingService parkingService;

	@Inject
	private UserService userService;

	@Inject
	private EntityManager entityManager;

	@Inject
	private Mapper mapper;

	@Inject
	private TestUtilities testUtilities;

	private Parking parking;
	private String parkingUuid;

	private String addedLogin1 = "user1";
	private String addedLogin2 = "user2";
	private String freeLogin3 = "user3";

	@Before
	public void prepareData() {
		testUtilities.persistUser(freeLogin3);
		parking = testUtilities.persistParkingWithUsers(addedLogin1, addedLogin2);
		parkingUuid = parking.getUuid();

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

	@Test(expected = Exception.class)
	public void shouldDeleteParking() {
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
	public void shouldNotAssignUser() {
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
	public void shouldNotRevokeUser() {
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

	@Test
	public void shouldHaveAssignedUser() {
		// when
		boolean assigned = parkingService.isUserAssigned(parkingUuid, addedLogin1);

		// then
		assertTrue(assigned);
	}

	@Test
	public void shouldNotHaveAssignedUser() {
		// when
		boolean assigned = parkingService.isUserAssigned(parkingUuid, freeLogin3);

		// then
		assertFalse(assigned);
	}

	@Test
	public void shouldDeleteRecordsOnCascade() {
		// when
		userService.deleteUser(addedLogin1);

		// then
		Collection<RecordDto> records = parkingService.findRecords(parkingUuid);
		assertNotNull(records);
		assertEquals(1, records.size());
	}

}
