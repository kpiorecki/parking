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

	private LocalDate date;
	private int availableCapacity;

	// TODO rename editable and enabled to more useful names
	private boolean editable = true;
	private boolean enabled = true;
	private boolean selected = false;
	private Status status = Status.EMPTY;
	
	// TODO add tooltip with accepted/rejected users
	private List<String> acceptedUsers = new ArrayList<>();
	private List<String> rejectedUsers = new ArrayList<>();

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getAvailableCapacity() {
		return availableCapacity;
	}

	public void setAvailableCapacity(int availableCapacity) {
		this.availableCapacity = availableCapacity;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
