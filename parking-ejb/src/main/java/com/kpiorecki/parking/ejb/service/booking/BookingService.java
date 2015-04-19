package com.kpiorecki.parking.ejb.service.booking;

import javax.ejb.Local;

import org.joda.time.LocalDate;

@Local
public interface BookingService {

	void book(String parkingUuid, String login, LocalDate date);

	void cancel(String parkingUuid, String login, LocalDate date);

	void release(String parkingUuid, LocalDate date);

	void lock(String parkingUuid, LocalDate date);
}
