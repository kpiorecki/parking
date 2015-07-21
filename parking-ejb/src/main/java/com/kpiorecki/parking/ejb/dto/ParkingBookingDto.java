package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDate;

public class ParkingBookingDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDate startDate;
	private LocalDate endDate;
	private ParkingDto parking;
	private List<BookingDto> bookingList;

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public ParkingDto getParking() {
		return parking;
	}

	public void setParking(ParkingDto parking) {
		this.parking = parking;
	}

	public List<BookingDto> getBookingList() {
		return bookingList;
	}

	public void setBookingList(List<BookingDto> bookingList) {
		this.bookingList = bookingList;
	}

}
