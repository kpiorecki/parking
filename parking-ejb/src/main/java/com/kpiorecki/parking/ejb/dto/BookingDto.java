package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.entity.BookingStatus;

public class BookingDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Set<BookingEntryDto> entries;
	private LocalDate date;
	private BookingStatus status;
	private boolean holiday;
	private List<String> notes;

	public Set<BookingEntryDto> getEntries() {
		return entries;
	}

	public void setEntries(Set<BookingEntryDto> entries) {
		this.entries = entries;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public boolean isHoliday() {
		return holiday;
	}

	public void setHoliday(boolean holiday) {
		this.holiday = holiday;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

}
