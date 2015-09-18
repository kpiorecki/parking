package com.kpiorecki.parking.ejb.service.holiday;

import java.util.List;

import javax.ejb.Local;

import com.kpiorecki.parking.ejb.dto.HolidayScheduleDto;

@Local
public interface HolidayScheduleService {

	String addSchedule(HolidayScheduleDto scheduleDto);

	void modifySchedule(HolidayScheduleDto scheduleDto);

	void deleteSchedule(String scheduleUuid);

	HolidayScheduleDto findSchedule(String scheduleUuid);

	List<HolidayScheduleDto> findAllSchedules();

}