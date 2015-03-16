package com.kpiorecki.parking.ejb.service;

import org.joda.time.LocalDate;

public interface BookingService {

	void book(String parkingUuid, String login, LocalDate date);

	void cancel(String parkingUuid, String login, LocalDate date);
}
