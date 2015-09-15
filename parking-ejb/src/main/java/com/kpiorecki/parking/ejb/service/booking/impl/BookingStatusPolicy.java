package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.util.Property;

@Stateless
public class BookingStatusPolicy {

	@Inject
	@Property(value = "bookingStatusPolicy.lock.hour", minIntValue = 0, maxIntValue = 23)
	private int lockHour = 12;

	public BookingStatus getDefaultStatus(LocalDate bookingDate, DateTime now) {
		// lock deadline is the day before date with hour set to lockHour
		LocalTime lockTime = LocalTime.MIDNIGHT.withHourOfDay(lockHour);
		DateTime lockDeadline = bookingDate.minusDays(1).toDateTime(lockTime);

		// status 'LOCKED' when lockDeadline has passed
		if (now.isAfter(lockDeadline)) {
			return BookingStatus.LOCKED;
		}

		// status 'RELEASED' when we're in the same week as date
		if (bookingDate.getWeekOfWeekyear() == now.getWeekOfWeekyear()
				&& bookingDate.getWeekyear() == now.getWeekyear()) {
			return BookingStatus.RELEASED;
		}

		// status 'DRAFT' otherwise
		return BookingStatus.DRAFT;
	}

}
