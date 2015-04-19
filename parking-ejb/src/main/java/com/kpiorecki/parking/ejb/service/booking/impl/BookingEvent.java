package com.kpiorecki.parking.ejb.service.booking.impl;

import java.io.Serializable;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.entity.Booking.Status;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;

public class BookingEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDate date;
	private User user;
	private Parking parking;
	private Status bookingStatus;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Parking getParking() {
		return parking;
	}

	public void setParking(Parking parking) {
		this.parking = parking;
	}

	public Status getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(Status bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

}
