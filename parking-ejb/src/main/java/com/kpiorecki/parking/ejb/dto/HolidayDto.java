package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

import org.joda.time.LocalDate;

public class HolidayDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int NOTE_MAX_LEN = 255;

	private LocalDate date;
	private Boolean repeatedEveryYear;
	private String note;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Boolean getRepeatedEveryYear() {
		return repeatedEveryYear;
	}

	public void setRepeatedEveryYear(Boolean repeatedEveryYear) {
		this.repeatedEveryYear = repeatedEveryYear;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HolidayDto [date=");
		builder.append(date);
		builder.append(", repeatedEveryYear=");
		builder.append(repeatedEveryYear);
		builder.append(", note=");
		builder.append(note);
		builder.append("]");
		return builder.toString();
	}

}
