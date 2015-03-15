package com.kpiorecki.parking.ejb.service;

import org.joda.time.DateTime;

public interface BookingService {

	void book(String parkingUuid, String login, DateTime date);

	void cancel(String parkingUuid, String login, DateTime date);
}
