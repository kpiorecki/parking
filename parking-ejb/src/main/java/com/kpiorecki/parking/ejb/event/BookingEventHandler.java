package com.kpiorecki.parking.ejb.event;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
public class BookingEventHandler {

	@Inject
	private Logger logger;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	public void onAssignedEvent(@Observes @Assigned BookingEvent event) {
		logger.info("received assigned {}", toString(event));
		// TODO event handling
	}

	public void onRevokedEvent(@Observes @Revoked BookingEvent event) {
		logger.info("received revoked {}", toString(event));
		// TODO event handling
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
