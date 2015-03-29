package com.kpiorecki.parking.ejb.service;

import javax.ejb.Local;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.ejb.dto.ScheduleDto;

@Local
public interface ScheduleService {

	ScheduleDto createSchedule(String parkingUuid, LocalDate date);

	ScheduleDto releaseSchedule(String parkingUuid, LocalDate date);

	ScheduleDto getSchedule(String parkingUuid, LocalDate date);

}
