package com.kpiorecki.parking.ejb.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Test;

import com.kpiorecki.parking.ejb.entity.HolidaySchedule.DateStatus;

public class HolidayScheduleTest {

	@Test
	public void shouldContainNoDaysOfWeek() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();

		// when
		List<Integer> daysOfWeek = schedule.getDaysOfWeek();

		// then
		assertTrue(daysOfWeek.isEmpty());
	}

	@Test
	public void shouldContainAllDaysOfWeek() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();
		for (int day = DateTimeConstants.MONDAY; day <= DateTimeConstants.SUNDAY; ++day) {
			schedule.addDayOfWeek(day);
		}

		// when
		List<Integer> daysOfWeek = schedule.getDaysOfWeek();

		// then
		assertEquals(7, daysOfWeek.size());
		for (int day = DateTimeConstants.MONDAY; day <= DateTimeConstants.SUNDAY; ++day) {
			assertTrue(daysOfWeek.contains(day));
		}
	}

	@Test
	public void shouldContainSaturdaySunday() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();

		// when
		schedule.addDayOfWeek(DateTimeConstants.SATURDAY);
		schedule.addDayOfWeek(DateTimeConstants.SUNDAY);

		// then
		for (int day = DateTimeConstants.MONDAY; day <= DateTimeConstants.FRIDAY; ++day) {
			assertFalse(schedule.containsDayOfWeek(day));
		}
		assertTrue(schedule.containsDayOfWeek(DateTimeConstants.SATURDAY));
		assertTrue(schedule.containsDayOfWeek(DateTimeConstants.SUNDAY));
	}

	@Test
	public void shouldClearDaysOfWeek() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);
		schedule.addDayOfWeek(DateTimeConstants.WEDNESDAY);

		// when
		schedule.clearDaysOfWeek();

		// then
		assertTrue(schedule.getDaysOfWeek().isEmpty());
	}

	@Test
	public void shouldAddDayOfWeekOnce() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();

		// when
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);

		// then
		assertTrue(schedule.containsDayOfWeek(DateTimeConstants.MONDAY));
		assertEquals(1, schedule.getDaysOfWeek().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAddInvalidDayOfWeek() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();

		// when
		schedule.addDayOfWeek(8);

		// then exception should be thrown
	}

	@Test
	public void shouldGetHolidayStatusForDayOfWeek() {
		// given
		HolidaySchedule schedule = new HolidaySchedule();
		schedule.addDayOfWeek(DateTimeConstants.TUESDAY);

		// when
		DateStatus status = schedule.getDateStatus(new LocalDate(2015, 7, 21));

		// then
		assertTrue(status.isHoliday());
		assertTrue(status.getNotes().isEmpty());
	}

	@Test
	public void shouldGetHolidayStatusForDate() {
		// given
		LocalDate date = new LocalDate(2015, 7, 21);

		Holiday holiday = new Holiday();
		holiday.setDate(date);
		holiday.setNote("note");

		HolidaySchedule schedule = new HolidaySchedule();
		schedule.addHoliday(holiday);

		// when
		DateStatus status = schedule.getDateStatus(date);

		// then
		assertTrue(status.isHoliday());
		assertEquals(1, status.getNotes().size());
		assertEquals("note", status.getNotes().get(0));
	}

	@Test
	public void shouldGetHolidayStatusForDaysOfWeekAndDates() {
		// given
		LocalDate date = new LocalDate(2015, 7, 21);

		Holiday holiday1 = new Holiday();
		holiday1.setDate(date);
		holiday1.setNote("note1");

		Holiday holiday2 = new Holiday();
		holiday2.setDate(date);
		holiday2.setNote("note2");

		Holiday holiday3 = new Holiday();
		holiday3.setDate(date.plusDays(1));
		holiday3.setNote("note3");

		HolidaySchedule schedule = new HolidaySchedule();
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);
		schedule.addDayOfWeek(DateTimeConstants.TUESDAY);
		schedule.addHoliday(holiday1);
		schedule.addHoliday(holiday2);
		schedule.addHoliday(holiday3);

		// when
		DateStatus status = schedule.getDateStatus(date);

		// then
		assertTrue(status.isHoliday());
		assertEquals(2, status.getNotes().size());
		assertTrue(status.getNotes().contains("note1"));
		assertTrue(status.getNotes().contains("note2"));
		assertFalse(status.getNotes().contains("note3"));
	}

	@Test
	public void shouldGetHolidayStatusForRepeatedDate() {
		// given
		LocalDate date = new LocalDate(2015, 7, 21);

		Holiday holiday = new Holiday();
		holiday.setRepeatedEveryYear(true);
		holiday.setDate(date);

		HolidaySchedule schedule = new HolidaySchedule();
		schedule.addHoliday(holiday);

		// when
		DateStatus status1 = schedule.getDateStatus(date);
		DateStatus status2 = schedule.getDateStatus(new LocalDate(2020, 7, 21));
		DateStatus status3 = schedule.getDateStatus(new LocalDate(2014, 7, 21));

		// then
		assertTrue(status1.isHoliday());
		assertTrue(status2.isHoliday());
		assertTrue(status3.isHoliday());
	}

	@Test
	public void shouldGetNormalStatus() {
		// given
		LocalDate date = new LocalDate(2015, 7, 21);

		Holiday holiday = new Holiday();
		holiday.setDate(date);

		HolidaySchedule schedule = new HolidaySchedule();
		schedule.addDayOfWeek(DateTimeConstants.MONDAY);
		schedule.addHoliday(holiday);

		// when
		DateStatus status = schedule.getDateStatus(date.plusDays(1));

		// then
		assertFalse(status.isHoliday());
		assertTrue(status.getNotes().isEmpty());
	}
}
