package com.kpiorecki.parking.web.user.model;

import java.util.List;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.entity.BookingStatus;

public class DayModel {

	public enum Status {
		EMPTY,
		REJECTED,
		ACCEPTED;
	}

	private int availableCapacity;
	private boolean holiday;

	private LocalDate date;
	private Status status;
	private BookingStatus bookingStatus;

	private List<String> holidayNotes;

	// TODO process users or delete it
	private List<String> acceptedUsers;
	private List<String> rejectedUsers;

	public int getAvailableCapacity() {
		return availableCapacity;
	}

	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}

	public boolean isHoliday() {
		return holiday;
	}

	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public boolean isSelected() {
		return status != Status.EMPTY;
	}

	public boolean isAccepted() {
		return status == Status.ACCEPTED;
	}

	public boolean isRejected() {
		return status == Status.REJECTED;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isLocked() {
		return bookingStatus == BookingStatus.LOCKED;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public List<String> getHolidayNotes() {
		return holidayNotes;
	}

	public void setHolidayNotes(List<String> holidayNotes) {
		this.holidayNotes = holidayNotes;
	}

	public List<String> getAcceptedUsers() {
		return acceptedUsers;
	}

	public void setAcceptedUsers(List<String> acceptedUsers) {
		this.acceptedUsers = acceptedUsers;
	}

	public List<String> getRejectedUsers() {
		return rejectedUsers;
	}

	public void setRejectedUsers(List<String> rejectedUsers) {
		this.rejectedUsers = rejectedUsers;
	}

}
