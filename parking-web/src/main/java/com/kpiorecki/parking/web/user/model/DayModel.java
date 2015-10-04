package com.kpiorecki.parking.web.user.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

public class DayModel {

	public enum Status {
		EMPTY,
		REJECTED,
		ACCEPTED;
	}

	private int availableCapacity;
	private boolean locked = false;
	private boolean holiday = false;
	private boolean selected = false;

	private Status status = Status.EMPTY;
	private LocalDate date;

	// TODO add tooltip with accepted/rejected users
	private List<String> acceptedUsers = new ArrayList<>();
	private List<String> rejectedUsers = new ArrayList<>();

	public int getAvailableCapacity() {
		return availableCapacity;
	}

	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isHoliday() {
		return holiday;
	}

	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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
