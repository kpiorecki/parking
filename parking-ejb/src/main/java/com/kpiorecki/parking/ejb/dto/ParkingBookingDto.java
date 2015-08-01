package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;
import java.util.List;

public class ParkingBookingDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private ParkingDto parking;
	private List<BookingDto> bookingList;

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
