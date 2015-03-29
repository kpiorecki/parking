package com.kpiorecki.parking.ejb.service;

import javax.ejb.Local;

import org.joda.time.LocalDate;

@Local
public interface BookingService {

	void book(String parkingUuid, String login, LocalDate date);

	void cancel(String parkingUuid, String login, LocalDate date);
}
