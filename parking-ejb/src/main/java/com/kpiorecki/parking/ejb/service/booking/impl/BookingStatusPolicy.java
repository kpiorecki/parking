package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.util.Property;

/**
 * The policy for defining {@link BookingStatus} for given bookingDate. The statuses are assigned the following way:
 * <ul>
 * <li>{@link BookingStatus#LOCKED} - past days, today, and tomorrow if it's after {@link #lockHour}</li>
 * <li>{@link BookingStatus#RELEASED} - non locked days from this week and possibly next week (only if today's day of
 * week is at least {@link #releaseDayOfWeek} and it's after {@link #releaseHour}</li>
 * <li>{@link BookingStatus#DRAFT} - all remaining days</li>
 * </ul>
 */
@Stateless
public class BookingStatusPolicy {

	@Inject
	@Property(value = "bookingStatusPolicy.release.dayOfWeek", minIntValue = DateTimeConstants.MONDAY, maxIntValue = DateTimeConstants.SUNDAY)
	private int releaseDayOfWeek = 3;

	@Inject
	@Property(value = "bookingStatusPolicy.release.hour", minIntValue = 0, maxIntValue = 23)
	private int releaseHour = 11;

	@Inject
	@Property(value = "bookingStatusPolicy.lock.hour", minIntValue = 0, maxIntValue = 23)
	private int lockHour = 12;

	public BookingStatus getDefaultStatus(LocalDate bookingDate, DateTime now) {
		if (isAfterLockDeadline(bookingDate, now)) {
			return BookingStatus.LOCKED;
		} else if (isBeforeReleaseEnd(bookingDate, now)) {
			return BookingStatus.RELEASED;
		} else {
			return BookingStatus.DRAFT;
		}
	}

	public LocalDate getLastLockedDate(DateTime now) {
		// last locked date is today for hours before lockTime, otherwise tomorrow
		LocalDate today = now.toLocalDate();
		if (now.toLocalTime().isAfter(getLockTime())) {
			return today.plusDays(1);
		} else {
			return today;
		}
	}

	public LocalTime getLockTime() {
		return LocalTime.MIDNIGHT.withHourOfDay(lockHour);
	}

	private boolean isAfterLockDeadline(LocalDate bookingDate, DateTime now) {
		// lock deadline is the day before date with hour set to lockHour
		DateTime lockDeadline = bookingDate.minusDays(1).toDateTime(getLockTime());
		return now.isAfter(lockDeadline);
	}

	private boolean isBeforeReleaseEnd(LocalDate bookingDate, DateTime now) {
		// find release end for now date
		LocalTime releaseTime = LocalTime.MIDNIGHT.withHourOfDay(releaseHour);
		LocalDate releaseDay = now.toLocalDate().withDayOfWeek(releaseDayOfWeek);
		DateTime releaseDate = releaseDay.toDateTime(releaseTime);

		LocalDate releaseEnd = null;
		if (now.isBefore(releaseDate)) {
			// release end is the end of releaseDay's week
			releaseEnd = getFutureMonday(releaseDay, 1);
		} else {
			// release end is the end of releaseDay's next week
			releaseEnd = getFutureMonday(releaseDay, 2);
		}

		return bookingDate.isBefore(releaseEnd);
	}

	private LocalDate getFutureMonday(LocalDate releaseDay, int futureWeeks) {
		return releaseDay.plusWeeks(futureWeeks).withDayOfWeek(DateTimeConstants.MONDAY);
	}

}
