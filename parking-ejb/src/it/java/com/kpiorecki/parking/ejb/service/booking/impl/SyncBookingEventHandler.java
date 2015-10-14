package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Specializes;

/**
 * Specialized BookingEventHandler that handles events synchronously
 */
@Specializes
@Stateless
public class SyncBookingEventHandler extends BookingEventHandler {

	public void onAssignedEvent(@Observes @BookingAssigned BookingEvent event) {
		super.onAssignedEvent(event);
	}

	public void onRevokedEvent(@Observes @BookingRevoked BookingEvent event) {
		super.onRevokedEvent(event);
	}

}
