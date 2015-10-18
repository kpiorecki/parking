package com.kpiorecki.parking.ejb.jpa;

import javax.inject.Inject;
import javax.persistence.PostLoad;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.service.booking.impl.BookingStatusPolicy;
import com.kpiorecki.parking.ejb.util.DateFormatter;

public class BookingUpdateStatusListener {

	@Inject
	private Logger logger;

	@Inject
	private BookingStatusPolicy statusPolicy;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@PostLoad
	public void updateBookingStatus(Object object) {
		if (object instanceof Booking) {
			Booking booking = (Booking) object;
			LocalDate date = booking.getDate();
			BookingStatus defaultStatus = statusPolicy.getDefaultStatus(date, new DateTime());

			logger.info("updating booking id={}, date={} status with default={}", booking.getId(),
					dateFormatter.print(date), defaultStatus);
			booking.updateStatus(defaultStatus);
		}
	}
}
