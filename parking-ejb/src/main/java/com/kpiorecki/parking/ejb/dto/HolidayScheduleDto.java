package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class HolidayScheduleDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int NAME_MAX_LEN = 255;

	private String uuid;
	private String name;
	private Integer version;
	private Set<ParkingDto> parkings;
	private Set<HolidayDto> holidays;
	private List<Integer> daysOfWeek;

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Set<ParkingDto> getParkings() {
		return parkings;
	}

	public void setParkings(Set<ParkingDto> parkings) {
		this.parkings = parkings;
	}

	public Set<HolidayDto> getHolidays() {
		return holidays;
	}

	public void setHolidays(Set<HolidayDto> holidays) {
		this.holidays = holidays;
	}

	public List<Integer> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(List<Integer> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HolidayScheduleDto [uuid=");
		builder.append(uuid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", version=");
		builder.append(version);
		builder.append(", daysOfWeek=");
		builder.append(daysOfWeek);
		builder.append("]");

		return builder.toString();
	}

}
