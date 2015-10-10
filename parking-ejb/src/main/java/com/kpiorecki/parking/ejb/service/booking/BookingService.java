package com.kpiorecki.parking.ejb.service.booking;

import java.util.List;

import javax.ejb.Local;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;

@Local
public interface BookingService {

	void book(String parkingUuid, String login, LocalDate date);

	void cancel(String parkingUuid, String login, LocalDate date);

	void release(String parkingUuid, LocalDate date);

	void lock(String parkingUuid, LocalDate date);
	
	ParkingBookingDto findBookings(String parkingName, String login, LocalDate startDate, LocalDate endDate);

	List<ParkingBookingDto> findAllBookings(String login, LocalDate startDate, LocalDate endDate);

}
