package com.kpiorecki.parking.ejb.service.booking.impl;

import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import static org.joda.time.DateTimeConstants.THURSDAY;
import static org.joda.time.DateTimeConstants.TUESDAY;
import static org.joda.time.DateTimeConstants.WEDNESDAY;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
		// given
		DateTime monday = new DateTime(2015, 5, 18, 0, 0);
		DateTime wednesdayBeforeRelease = monday.withDayOfWeek(WEDNESDAY).withHourOfDay(10).withMinuteOfHour(59)
				.withSecondOfMinute(59);
		DateTime wednesdayAfterRelease = wednesdayBeforeRelease.plusMinutes(2);
		LocalDate localDate = monday.toLocalDate();

		// when (by default releaseHour == 11, releaseDay == WEDNESDAY)
		List<BookingStatus> statuses = new LinkedList<>();
		for (int day = TUESDAY; day <= SUNDAY; ++day) {
			statuses.add(policy.getDefaultStatus(localDate.withDayOfWeek(day), monday));
		}
		for (int day = THURSDAY; day <= SUNDAY; ++day) {
			statuses.add(policy.getDefaultStatus(localDate.withDayOfWeek(day), wednesdayBeforeRelease));
			statuses.add(policy.getDefaultStatus(localDate.withDayOfWeek(day), wednesdayAfterRelease));
		}
		for (int day = MONDAY; day <= SUNDAY; ++day) {
			statuses.add(policy.getDefaultStatus(localDate.withDayOfWeek(day).plusWeeks(1), wednesdayAfterRelease));
		}

		// then
		assertStatus(BookingStatus.RELEASED, statuses);
	}

	@Test
	public void shouldGetStatusDraft() {
		// given
		DateTime monday = new DateTime(2015, 5, 18, 0, 0);
		DateTime wednesdayBeforeRelease = monday.withDayOfWeek(WEDNESDAY).withHourOfDay(10).withMinuteOfHour(59)
				.withSecondOfMinute(59);
		DateTime wednesdayAfterRelease = wednesdayBeforeRelease.plusMinutes(2);
		LocalDate nextMonday = monday.toLocalDate().plusWeeks(1);

		// when (by default releaseHour == 11, releaseDay == WEDNESDAY)
		List<BookingStatus> statuses = new LinkedList<>();
		for (int day = TUESDAY; day <= SUNDAY; ++day) {
			statuses.add(policy.getDefaultStatus(nextMonday.withDayOfWeek(day), monday));
		}
		for (int day = THURSDAY; day <= SUNDAY; ++day) {
			statuses.add(policy.getDefaultStatus(nextMonday.withDayOfWeek(day), wednesdayBeforeRelease));
		}
		for (int day = MONDAY; day <= SUNDAY; ++day) {
			statuses.add(policy.getDefaultStatus(nextMonday.withDayOfWeek(day).plusWeeks(1), wednesdayAfterRelease));
		}

		statuses.add(policy.getDefaultStatus(new LocalDate(2015, 7, 10), monday));
		statuses.add(policy.getDefaultStatus(new LocalDate(2016, 1, 1), monday));
		statuses.add(policy.getDefaultStatus(new LocalDate(2035, 9, 14), monday));

		// then
		assertStatus(BookingStatus.DRAFT, statuses);
	}

	private void assertStatus(BookingStatus status, List<BookingStatus> statuses) {
		assertTrue(statuses.stream().allMatch(e -> status.equals(e)));
	}
}
