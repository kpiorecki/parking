package com.kpiorecki.parking.ejb.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

public class HolidayTest {

	@Test
	public void shouldMatchDate() {
		// given
		Holiday holiday = new Holiday();

		// when
		holiday.setDate(new LocalDate(2015, 7, 10));

		// then
		assertTrue(holiday.matches(new LocalDate(2015, 7, 10)));
	}

	@Test
	public void shouldNotMatchDates() {
		// given
		Holiday holiday = new Holiday();

		// when
		holiday.setDate(new LocalDate(2015, 7, 10));

		// then
		assertFalse(holiday.matches(new LocalDate(2015, 7, 11)));
		assertFalse(holiday.matches(new LocalDate(2015, 7, 12)));
		assertFalse(holiday.matches(new LocalDate(2014, 7, 10)));
		assertFalse(holiday.matches(new LocalDate(2015, 8, 10)));
	}

	@Test
	public void shouldMatchEveryYearDates() {
		// given
		Holiday holiday = new Holiday();
		holiday.setRepeatedEveryYear(true);

		// when
		holiday.setDate(new LocalDate(2015, 7, 10));

		// then
		assertTrue(holiday.matches(new LocalDate(2015, 7, 10)));
		assertTrue(holiday.matches(new LocalDate(2014, 7, 10)));
		assertTrue(holiday.matches(new LocalDate(2013, 7, 10)));
		assertTrue(holiday.matches(new LocalDate(2016, 7, 10)));
		assertTrue(holiday.matches(new LocalDate(2017, 7, 10)));
	}

	@Test
	public void shouldNotMatchEveryYearDates() {
		// given
		Holiday holiday = new Holiday();
		holiday.setRepeatedEveryYear(true);

		// when
		holiday.setDate(new LocalDate(2015, 7, 10));

		// then
		assertFalse(holiday.matches(new LocalDate(2015, 7, 11)));
		assertFalse(holiday.matches(new LocalDate(2015, 7, 12)));
		assertFalse(holiday.matches(new LocalDate(2014, 7, 11)));
	}

	@Test
	public void shouldMatchEveryYearFeb29Properly() {
		// given
		Holiday holiday = new Holiday();
		holiday.setRepeatedEveryYear(true);

		// when
		holiday.setDate(new LocalDate(2016, 2, 29));

		// then
		assertTrue(holiday.matches(new LocalDate(2020, 2, 29)));
		assertTrue(holiday.matches(new LocalDate(2016, 2, 29)));
		assertTrue(holiday.matches(new LocalDate(2012, 2, 29)));
		assertFalse(holiday.matches(new LocalDate(2017, 3, 1)));
		assertFalse(holiday.matches(new LocalDate(2015, 2, 28)));
	}

	@Test
	public void shouldMatchEveryYearFeb28Properly() {
		// given
		Holiday holiday = new Holiday();
		holiday.setRepeatedEveryYear(true);

		// when
		holiday.setDate(new LocalDate(2015, 2, 28));

		// then
		assertFalse(holiday.matches(new LocalDate(2016, 2, 29)));
		assertTrue(holiday.matches(new LocalDate(2016, 2, 28)));
	}
}
