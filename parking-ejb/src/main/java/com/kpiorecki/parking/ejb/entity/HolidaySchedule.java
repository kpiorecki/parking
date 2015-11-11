package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.dto.HolidayScheduleBaseDto;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@Entity
@Table(name = "holiday_schedules")
public class HolidaySchedule implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int ALL_DAYS_OF_WEEK_MASK = 0b1111111;

	public static class DateStatus {

		private boolean holiday;
		private List<String> notes;

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

	@Id
	@Column(length = UuidGenerator.UUID_LENGTH)
	private String uuid;

	@Column(nullable = false, length = HolidayScheduleBaseDto.NAME_MAX_LEN)
	private String name;

	@OneToMany(mappedBy = "holidaySchedule", cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	private Set<Parking> parkings = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	@JoinColumn(name = "holiday_schedule_uuid")
	private Set<Holiday> holidays = new HashSet<>();

	@Column
	private Integer dayOfWeekMask;

	@Version
	private Integer version;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Parking> getParkings() {
		return new HashSet<Parking>(parkings);
	}

	public void addParking(Parking parking) {
		parkings.add(parking);
		parking.setHolidaySchedule(this);
	}

	public void removeParking(Parking parking) {
		boolean removed = parkings.remove(parking);
		if (removed) {
			parking.setHolidaySchedule(null);
		}
	}

	@PreRemove
	public void removeAllParkings() {
		Set<Parking> parkings = getParkings();
		for (Parking parking : parkings) {
			removeParking(parking);
		}
	}

	public Set<Holiday> getHolidays() {
		return new HashSet<Holiday>(holidays);
	}

	public void setHolidays(Set<Holiday> holidays) {
		this.holidays.clear();
		if (holidays != null) {
			this.holidays.addAll(holidays);
		}
	}

	public void addHoliday(Holiday holiday) {
		holidays.add(holiday);
	}

	public List<Integer> getDaysOfWeek() {
		List<Integer> daysOfWeek = new ArrayList<>();
		if (dayOfWeekMask != null) {
			for (int day = DateTimeConstants.MONDAY; day <= DateTimeConstants.SUNDAY; ++day) {
				if (containsDayOfWeek(day)) {
					daysOfWeek.add(day);
				}
			}
		}
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<Integer> daysOfWeek) {
		clearDaysOfWeek();
		if (daysOfWeek != null) {
			for (Integer dayOfWeek : daysOfWeek) {
				addDayOfWeek(dayOfWeek);
			}
		}
	}

	public void clearDaysOfWeek() {
		dayOfWeekMask = null;
	}

	public boolean containsDayOfWeek(int dayOfWeek) {
		validateDayOfWeek(dayOfWeek);
		if (dayOfWeekMask == null) {
			return false;
		}
		return (dayOfWeekMask & getDayOfWeekMarker(dayOfWeek)) != 0;
	}

	public void addDayOfWeek(int dayOfWeek) {
		validateDayOfWeek(dayOfWeek);

		Integer newDayOfWeekMask = dayOfWeekMask;
		if (newDayOfWeekMask == null) {
			newDayOfWeekMask = 0;
		}
		newDayOfWeekMask |= getDayOfWeekMarker(dayOfWeek);

		validateDayOfWeekMask(newDayOfWeekMask);
		dayOfWeekMask = newDayOfWeekMask;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public DateStatus getDateStatus(LocalDate date) {
		boolean matches = containsDayOfWeek(date.getDayOfWeek());

		List<String> notes = new ArrayList<>();
		for (Holiday holiday : holidays) {
			if (holiday.matches(date)) {
				matches = true;
				String note = holiday.getNote();
				if (!StringUtils.isEmpty(note)) {
					notes.add(note);
				}
			}
		}

		Collections.sort(notes, String.CASE_INSENSITIVE_ORDER);

		DateStatus status = new DateStatus();
		status.setHoliday(matches);
		status.setNotes(notes);

		return status;
	}

	private int getDayOfWeekMarker(int dayOfWeek) {
		return 1 << (dayOfWeek - 1);
	}

	private void validateDayOfWeek(int dayOfWeek) {
		if (dayOfWeek < DateTimeConstants.MONDAY || dayOfWeek > DateTimeConstants.SUNDAY) {
			throw new IllegalArgumentException(String.format("invalid day of week value %d", dayOfWeek));
		}
	}

	private void validateDayOfWeekMask(Integer dayOfWeekMask) {
		if ((ALL_DAYS_OF_WEEK_MASK & dayOfWeekMask) == ALL_DAYS_OF_WEEK_MASK) {
			throw new IllegalArgumentException("holiday schedule cannot contain all days of week");
		}
	}
}
