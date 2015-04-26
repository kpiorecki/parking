package com.kpiorecki.parking.ejb.service.booking.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.MailSender;

@Stateless
public class BookingEventHandler {

	@Inject
	private Logger logger;

	@Inject
	private MailSender mailSender;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	public void onAssignedEvent(@Observes @BookingAssigned BookingEvent event) {
		logger.info("received assigned {}", toString(event));
		mailSender.send(event.getUser(), "Parking booking assigned", "booking/booking-assigned.ftl",
				toParameters(event));
	}

	public void onRevokedEvent(@Observes @BookingRevoked BookingEvent event) {
		logger.info("received revoked {}", toString(event));
		mailSender.send(event.getUser(), "Parking booking revoked", "booking/booking-revoked.ftl",
				toParameters(event));
	}

	private Map<String, Object> toParameters(BookingEvent event) {
		Map<String, Object> map = new HashMap<>();
		map.put("user", event.getUser());
		map.put("date", event.getDate());
		map.put("parking", event.getParking());
		map.put("status", event.getBookingStatus());

		return map;
	}

	private String toString(BookingEvent event) {
		StringBuilder sb = new StringBuilder();
		sb.append("booking event[date=");
		sb.append(dateFormatter.print(event.getDate()));
		sb.append(", login=");
		sb.append(event.getUser().getLogin());
		sb.append(", parking=");
		sb.append(event.getParking().getUuid());
		sb.append(", status=");
		sb.append(event.getBookingStatus());
		sb.append(']');

		return sb.toString();
	}
}
