package com.kpiorecki.parking.web.user.model;

import java.util.List;

import org.joda.time.LocalDate;

public class DayModel {

	private int availableCapacity;
	private boolean holiday;
	private boolean selected;
	private boolean locked;
	private boolean accepted;
	private boolean rejected;

	private LocalDate date;
	private List<String> notes;

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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

}
