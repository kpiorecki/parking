package com.kpiorecki.parking.ejb.dto;

import java.util.List;
import java.util.Set;

public class HolidayScheduleDto extends HolidayScheduleBaseDto {

	private static final long serialVersionUID = 1L;

	private Integer version;
	private Set<ParkingDto> parkings;
	private Set<HolidayDto> holidays;
	private List<Integer> daysOfWeek;

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
		builder.append(getUuid());
		builder.append(", name=");
		builder.append(getName());
		builder.append(", version=");
		builder.append(version);
		builder.append(", daysOfWeek=");
		builder.append(daysOfWeek);
		builder.append("]");

		return builder.toString();
	}

}
