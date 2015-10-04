package com.kpiorecki.parking.ejb.service.holiday;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import com.kpiorecki.parking.ejb.entity.HolidaySchedule;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;

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

		ParkingDto parkingDto = mapper.map(parking, ParkingDto.class);
		LocalDate date1 = new LocalDate(2015, 10, 14);
		LocalDate date2 = new LocalDate(2015, 10, 15);
		int[] daysOfWeek = new int[] { DateTimeConstants.SATURDAY, DateTimeConstants.SUNDAY };

		HolidayScheduleDto scheduleDto = new HolidayScheduleDto();
		scheduleDto.setName("schedule");
		setDaysOfWeek(scheduleDto, daysOfWeek);
		setHolidays(scheduleDto, date1, date2);
		setParkings(scheduleDto, parkingDto);

		// when
		String scheduleUuid = scheduleService.addSchedule(scheduleDto);
		entityManager.flush();

		// then
		HolidayScheduleDto foundScheduleDto = scheduleService.findSchedule(scheduleUuid);

		validateSchedule(foundScheduleDto, scheduleUuid, "schedule");
		validateDaysOfWeek(foundScheduleDto, daysOfWeek);
		validateParkings(foundScheduleDto, parking.getUuid());
		validateHolidays(foundScheduleDto, date1, date2);
	}

	@Test
	public void shouldModifySchedule() {
		// given
		LocalDate date1 = new LocalDate(2015, 1, 1);
		LocalDate date2 = new LocalDate(2015, 2, 20);
		LocalDate date3 = new LocalDate(2015, 10, 12);

		Parking parking1 = testUtilities.persistParking();
		Parking parking2 = testUtilities.persistParking();
		Parking parking3 = testUtilities.persistParking();

		HolidaySchedule schedule = testUtilities.createSchedule(date1, date2);
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);
		schedule.addParking(parking1);
		schedule.addParking(parking2);

		entityManager.persist(schedule);
		entityManager.flush();

		ParkingDto parkingDto1 = mapper.map(parking1, ParkingDto.class);
		ParkingDto parkingDto3 = mapper.map(parking3, ParkingDto.class);

		int[] newDaysOfWeek = new int[] { DateTimeConstants.SATURDAY, DateTimeConstants.SUNDAY };

		HolidayScheduleDto scheduleDto = new HolidayScheduleDto();
		scheduleDto.setUuid(schedule.getUuid());
		scheduleDto.setName("new schedule");
		scheduleDto.setVersion(schedule.getVersion());
		setDaysOfWeek(scheduleDto, newDaysOfWeek);
		setHolidays(scheduleDto, date1, date3);
		setParkings(scheduleDto, parkingDto1, parkingDto3);

		// when
		scheduleService.modifySchedule(scheduleDto);
		entityManager.flush();

		// then
		HolidayScheduleDto foundScheduleDto = scheduleService.findSchedule(schedule.getUuid());

		validateSchedule(foundScheduleDto, schedule.getUuid(), "new schedule");
		validateDaysOfWeek(foundScheduleDto, newDaysOfWeek);
		validateParkings(foundScheduleDto, parking1.getUuid(), parking3.getUuid());
		validateHolidays(foundScheduleDto, date1, date3);
	}

	@Test
	public void shouldDeleteSchedule() {
		// given
		User user = testUtilities.persistUser("login");
		Parking parking = testUtilities.persistParking(user);

		LocalDate date1 = new LocalDate(2015, 1, 1);
		LocalDate date2 = new LocalDate(2015, 2, 1);

		HolidaySchedule schedule = testUtilities.createSchedule(date1, date2);
		String scheduleUuid = schedule.getUuid();
		schedule.addParking(parking);

		entityManager.persist(schedule);
		entityManager.flush();

		// when
		scheduleService.deleteSchedule(scheduleUuid);
		entityManager.flush();

		// then
		HolidaySchedule foundSchedule = entityManager.find(HolidaySchedule.class, scheduleUuid);
		assertNull(foundSchedule);

		Parking foundParking = entityManager.find(Parking.class, parking.getUuid());
		assertNotNull(foundParking);
		assertEquals(parking.getUuid(), foundParking.getUuid());
		assertNull(foundParking.getHolidaySchedule());
	}

	@Test
	public void shouldFindSchedule() {
		// given
		Parking parking = testUtilities.persistParking();

		LocalDate date1 = new LocalDate(2015, 1, 1);
		LocalDate date2 = new LocalDate(2015, 2, 1);

		HolidaySchedule schedule = testUtilities.createSchedule(date1, date2);
		schedule.setName("schedule");
		schedule.addParking(parking);
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);
		schedule.addDayOfWeek(DateTimeConstants.WEDNESDAY);

		entityManager.persist(schedule);
		entityManager.flush();

		// when
		HolidayScheduleDto scheduleDto = scheduleService.findSchedule(schedule.getUuid());

		// then
		validateSchedule(scheduleDto, schedule.getUuid(), "schedule");
		validateDaysOfWeek(scheduleDto, DateTimeConstants.MONDAY, DateTimeConstants.WEDNESDAY);
		validateHolidays(scheduleDto, date1, date2);
	}

	@Test
	public void shouldFindAllSchedules() {
		// given
		int size = 10;
		List<String> scheduleUuids = new ArrayList<String>(size);
		for (int i = 0; i < size; ++i) {
			HolidaySchedule schedule = testUtilities.persistSchedule();
			scheduleUuids.add(schedule.getUuid());
		}

		entityManager.flush();

		// when
		List<HolidayScheduleDto> allSchedules = scheduleService.findAllSchedules();

		// then
		assertNotNull(allSchedules);
		assertEquals(size, allSchedules.size());
		for (String uuid : scheduleUuids) {
			assertTrue(allSchedules.stream().anyMatch(scheduleDto -> scheduleDto.getUuid().equals(uuid)));
		}
	}

	private void setDaysOfWeek(HolidayScheduleDto scheduleDto, int... days) {
		List<Integer> daysOfWeek = new ArrayList<>();
		for (int day : days) {
			daysOfWeek.add(day);
		}
		scheduleDto.setDaysOfWeek(daysOfWeek);
	}

	private void setHolidays(HolidayScheduleDto scheduleDto, LocalDate... dates) {
		Set<HolidayDto> holidays = new HashSet<HolidayDto>();
		for (LocalDate date : dates) {
			HolidayDto holidayDto = new HolidayDto();
			holidayDto.setDate(date);
			holidayDto.setRepeatedEveryYear(false);
			holidays.add(holidayDto);
		}
		scheduleDto.setHolidays(holidays);
	}

	private void setParkings(HolidayScheduleDto scheduleDto, ParkingDto... parkingDtoList) {
		Set<ParkingDto> parkings = new HashSet<ParkingDto>();
		for (ParkingDto parkingDto : parkingDtoList) {
			parkings.add(parkingDto);
		}
		scheduleDto.setParkings(parkings);
	}

	private void validateSchedule(HolidayScheduleDto scheduleDto, String expectedUuid, String expectedName) {
		assertNotNull(scheduleDto);
		assertEquals(expectedUuid, scheduleDto.getUuid());
		assertEquals(expectedName, scheduleDto.getName());
	}

	private void validateDaysOfWeek(HolidayScheduleDto scheduleDto, int... expectedDaysOfWeek) {
		List<Integer> daysOfWeek = scheduleDto.getDaysOfWeek();
		assertEquals(expectedDaysOfWeek.length, daysOfWeek.size());
		for (int day : expectedDaysOfWeek) {
			assertTrue(daysOfWeek.contains(day));
		}
	}

	private void validateParkings(HolidayScheduleDto scheduleDto, String... expectedParkingUuids) {
		Set<ParkingDto> parkings = scheduleDto.getParkings();
		assertEquals(expectedParkingUuids.length, parkings.size());
		for (String expectedUuid : expectedParkingUuids) {
			assertTrue(parkings.stream().anyMatch(parkingDto -> parkingDto.getUuid().equals(expectedUuid)));
		}
		for (ParkingDto parkingDto : parkings) {
			Parking parking = entityManager.find(Parking.class, parkingDto.getUuid());
			HolidaySchedule schedule = parking.getHolidaySchedule();
			assertNotNull(schedule);
			assertEquals(schedule.getUuid(), scheduleDto.getUuid());
		}
	}

	private void validateHolidays(HolidayScheduleDto foundScheduleDto, LocalDate... expectedDates) {
		Set<HolidayDto> holidays = foundScheduleDto.getHolidays();
		assertEquals(expectedDates.length, holidays.size());
		for (LocalDate expectedDate : expectedDates) {
			assertTrue(holidays.stream().anyMatch(holidayDto -> holidayDto.getDate().isEqual(expectedDate)));
		}
	}

}
