package com.kpiorecki.parking.core.service;

import org.joda.time.DateTime;

import com.kpiorecki.parking.core.dto.ScheduleDto;

public interface ScheduleService {

	ScheduleDto createSchedule(String parkingUuid, DateTime date);

	ScheduleDto releaseSchedule(String parkingUuid, DateTime date);

	ScheduleDto getSchedule(String parkingUuid, DateTime date);

}
