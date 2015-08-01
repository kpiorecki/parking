package com.kpiorecki.parking.web.user.model;

import java.util.List;

import com.kpiorecki.parking.ejb.dto.ParkingDto;

public class BookingModel {

	private ParkingDto parking;
	private List<DayModel> dayModels;
	private List<WeekModel> weekModels;

	public ParkingDto getParking() {
		return parking;
	}

	public void setParking(ParkingDto parking) {
		this.parking = parking;
	}

	public List<DayModel> getDayModels() {
		return dayModels;
	}

	public void setDayModels(List<DayModel> dayModels) {
		this.dayModels = dayModels;
	}

	public List<WeekModel> getWeekModels() {
		return weekModels;
	}

	public void setWeekModels(List<WeekModel> weekModels) {
		this.weekModels = weekModels;
	}

}
