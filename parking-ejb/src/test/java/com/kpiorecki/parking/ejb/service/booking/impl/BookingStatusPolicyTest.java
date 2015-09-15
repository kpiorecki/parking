package com.kpiorecki.parking.ejb.service.booking.impl;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import com.kpiorecki.parking.ejb.entity.BookingStatus;

public class BookingStatusPolicyTest {

	private BookingStatusPolicy policy = new BookingStatusPolicy();

	@Test
	public void shouldGetStatusLocked() {
		// given
		DateTime date = new DateTime(2015, 5, 1, 0, 0);
		LocalDate bookingDate = new LocalDate(2015, 5, 2);

		// when (by default lockHour == 12)
		List<BookingStatus> statuses = new LinkedList<>();
		statuses.add(policy.getDefaultStatus(bookingDate, date.withHourOfDay(12).withMillisOfSecond(1)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withHourOfDay(13)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.plusDays(1)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.plusYears(1)));

		// then
		assertStatus(BookingStatus.LOCKED, statuses);
	}

	@Test
	public void shouldGetStatusReleased() {
		// given date is Wednesday, bookingDate is Thursday
		DateTime date = new DateTime(2015, 5, 20, 0, 0);
		LocalDate bookingDate = new LocalDate(2015, 5, 21);

		// when (by default lockHour == 12)
		List<BookingStatus> statuses = new LinkedList<>();
		statuses.add(policy.getDefaultStatus(bookingDate, date.withHourOfDay(12)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withHourOfDay(11).withMinuteOfHour(58)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withDayOfMonth(18)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withDayOfMonth(18).withTime(LocalTime.MIDNIGHT)));

		// then
		assertStatus(BookingStatus.RELEASED, statuses);
	}

	@Test
	public void shouldGetStatusDraft() {
		// given date is Wednesday, bookingDate is Thursday
		DateTime date = new DateTime(2015, 5, 20, 0, 0);
		LocalDate bookingDate = new LocalDate(2015, 5, 21);

		// when
		List<BookingStatus> statuses = new LinkedList<>();
		statuses.add(policy.getDefaultStatus(bookingDate, date.withDayOfMonth(18).withTime(LocalTime.MIDNIGHT)
				.minusMillis(1)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withDayOfMonth(17)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withMonthOfYear(4).withDayOfMonth(30)));
		statuses.add(policy.getDefaultStatus(bookingDate, date.withYear(2014)));

		// then
		assertStatus(BookingStatus.DRAFT, statuses);
	}

	private void assertStatus(BookingStatus status, List<BookingStatus> statuses) {
		assertTrue(statuses.stream().allMatch(e -> status.equals(e)));
	}
}
