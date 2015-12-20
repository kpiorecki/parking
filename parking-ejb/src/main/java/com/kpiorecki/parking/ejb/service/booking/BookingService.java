package com.kpiorecki.parking.ejb.service.booking;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;

@Local
public interface BookingService {

	void book(String parkingUuid, String login, LocalDate date);

	void cancel(String parkingUuid, String login, LocalDate date);

	void update(String parkingUuid, String login, Set<LocalDate> bookedDates, Set<LocalDate> cancelledDates);

	void release(String parkingUuid, LocalDate date);

	void lock(String parkingUuid, LocalDate date);

	void lockAccordingToPolicy();

	ParkingBookingDto findBookings(String parkingName, String login, LocalDate startDate, LocalDate endDate);

	List<ParkingBookingDto> findAllBookings(String login, LocalDate startDate, LocalDate endDate);

}
