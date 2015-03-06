package com.kpiorecki.parking.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Test;

import com.kpiorecki.parking.core.dto.AddressDto;
import com.kpiorecki.parking.core.dto.ParkingDto;
import com.kpiorecki.parking.core.service.ParkingService;

public class ParkingServiceTest extends IntegrationTest {

	@Inject
	private ParkingService parkingService;

	@Inject
	private EntityManager entityManager;

	@Test
	public void shouldAddParking() {
		// given
		ParkingDto parking = createDefaultParking();

		// when
		String uuid = parkingService.addParking(parking);

		// then
		ParkingDto foundParking = parkingService.findParking(uuid);
		assertNotNull(foundParking);
	}

	@Test
	public void shouldModifyParking() {
		// given
		String uuid = parkingService.addParking(createDefaultParking());
		ParkingDto parking = parkingService.findParking(uuid);

		// when
		parking.setCapacity(100);
		parking.getAddress().setCity("new city");
		parkingService.modifyParking(parking);

		// then
		ParkingDto foundParking = parkingService.findParking(uuid);
		assertNotNull(foundParking);
		assertEquals((Integer) 100, foundParking.getCapacity());
		assertEquals("new city", foundParking.getAddress().getCity());
	}

	@Test
	public void shouldDeleteParking() {
		// given
		String uuid = parkingService.addParking(createDefaultParking());
		entityManager.flush();

		// when
		parkingService.deleteParking(uuid);

		// then
		ParkingDto parking = parkingService.findParking(uuid);
		assertNull(parking);
	}

	private ParkingDto createDefaultParking() {
		AddressDto address = new AddressDto();
		address.setCity("city");
		address.setPostalCode("code");
		address.setStreet("street");
		address.setNumber("number");

		ParkingDto parking = new ParkingDto();
		parking.setAddress(address);
		parking.setCapacity(50);
		parking.setName("name");

		return parking;
	}
}
