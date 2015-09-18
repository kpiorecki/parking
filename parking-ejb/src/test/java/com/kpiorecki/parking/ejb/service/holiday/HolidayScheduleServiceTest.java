package com.kpiorecki.parking.ejb.service.holiday;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.GlassFishSecuredTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dto.HolidayDto;
import com.kpiorecki.parking.ejb.dto.HolidayScheduleDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.entity.Parking;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class HolidayScheduleServiceTest extends GlassFishSecuredTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createFullDeployment();
	}

	@Inject
	private HolidayScheduleService scheduleService;

	@Inject
	private EntityManager entityManager;

	@Inject
	private Mapper mapper;

	@Inject
	private TestUtilities testUtilities;

	@Test
	public void shouldAddSchedule() {
		// given
		Parking parking = testUtilities.persistParking();
		entityManager.flush();

		// when
		ParkingDto parkingDto = mapper.map(parking, ParkingDto.class);

		LocalDate holidayDate = new LocalDate(2015, 1, 1);
		HolidayDto holidayDto = new HolidayDto();
		holidayDto.setDate(holidayDate);
		holidayDto.setRepeatedEveryYear(false);
		holidayDto.setNote("note");

		HolidayScheduleDto scheduleDto = new HolidayScheduleDto();
		scheduleDto.setName("schedule");
		scheduleDto.setDaysOfWeek(Arrays.asList(DateTimeConstants.MONDAY, DateTimeConstants.WEDNESDAY));
		scheduleDto.setParkings(new HashSet<ParkingDto>(Arrays.asList(parkingDto)));
		scheduleDto.setHolidays(new HashSet<HolidayDto>(Arrays.asList(holidayDto)));

		String scheduleUuid = scheduleService.addSchedule(scheduleDto);
		entityManager.flush();

		// then
		HolidayScheduleDto foundScheduleDto = scheduleService.findSchedule(scheduleUuid);
		assertNotNull(foundScheduleDto);
		assertEquals("schedule", foundScheduleDto.getName());

		List<Integer> foundDaysOfWeek = foundScheduleDto.getDaysOfWeek();
		assertEquals(2, foundDaysOfWeek.size());
		assertTrue(foundDaysOfWeek.contains(DateTimeConstants.MONDAY));
		assertTrue(foundDaysOfWeek.contains(DateTimeConstants.WEDNESDAY));

		Set<ParkingDto> foundParkings = foundScheduleDto.getParkings();
		assertEquals(1, foundParkings.size());
		ParkingDto foundParkingDto = foundParkings.iterator().next();
		assertEquals(parking.getUuid(), foundParkingDto.getUuid());

		Set<HolidayDto> foundHolidays = foundScheduleDto.getHolidays();
		assertEquals(1, foundHolidays.size());
		HolidayDto foundHolidayDto = foundHolidays.iterator().next();
		assertEquals(holidayDate, foundHolidayDto.getDate());
		assertEquals("note", foundHolidayDto.getNote());
	}

	// TODO add remaining tests

}
