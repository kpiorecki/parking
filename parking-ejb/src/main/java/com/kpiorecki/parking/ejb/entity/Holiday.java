package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;

import com.kpiorecki.parking.ejb.dto.HolidayDto;

@Entity
@Table(name = "holidays")
public class Holiday implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "seq_holidays")
	@TableGenerator(name = "seq_holidays", pkColumnValue = "seq_holidays")
	private Long id;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private Boolean repeatedEveryYear = false;

	@Column(length = HolidayDto.NOTE_MAX_LEN)
	private String note;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public boolean matches(LocalDate matchDate) {
		if (date == null) {
			return false;
		}
		if (repeatedEveryYear) {
			MonthDay monthDay = new MonthDay(date);
			MonthDay matchMonthDay = new MonthDay(matchDate);
			return monthDay.isEqual(matchMonthDay);
		} else {
			return date.isEqual(matchDate);
		}
	}
}
