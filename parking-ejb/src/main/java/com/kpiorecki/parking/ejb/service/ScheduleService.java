package com.kpiorecki.parking.ejb.service;

import org.joda.time.DateTime;

import com.kpiorecki.parking.ejb.dto.ScheduleDto;

public interface ScheduleService {

	ScheduleDto createSchedule(String parkingUuid, DateTime date);

	ScheduleDto releaseSchedule(String parkingUuid, DateTime date);

	ScheduleDto getSchedule(String parkingUuid, DateTime date);

}
