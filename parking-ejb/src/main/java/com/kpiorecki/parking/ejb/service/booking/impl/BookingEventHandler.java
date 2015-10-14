package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
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

	@Asynchronous
	public void onAssignedEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) @BookingAssigned BookingEvent event) {
		logger.info("received assigned {}", toString(event));
		mailSender.sendBookingAssignedMail(event.getUser(), event.getParking(), event.getDate());
	}

	@Asynchronous
	public void onRevokedEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) @BookingRevoked BookingEvent event) {
		logger.info("received revoked {}", toString(event));
		mailSender.sendBookingRevokedMail(event.getUser(), event.getParking(), event.getDate());
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
