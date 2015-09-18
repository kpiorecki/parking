package com.kpiorecki.parking.ejb.service.holiday.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.HolidayScheduleDao;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dto.HolidayScheduleDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.service.holiday.HolidayScheduleService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;
import com.kpiorecki.parking.ejb.util.Role;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@Stateless
@RolesAllowed(Role.ADMIN)
public class HolidayScheduleServiceImpl implements HolidayScheduleService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private CollectionMapper collectionMapper;

	@Inject
	private UuidGenerator uuidGenerator;

	@Inject
	private HolidayScheduleDao scheduleDao;

	@Inject
	private ParkingDao parkingDao;

	@Override
	public String addSchedule(HolidayScheduleDto scheduleDto) {
		logger.info("adding {}", scheduleDto);

		HolidaySchedule schedule = mapper.map(scheduleDto, HolidaySchedule.class);
		fillEntityParkings(schedule, scheduleDto);
		if (schedule.getUuid() == null) {
			schedule.setUuid(uuidGenerator.generateUuid());
		}

		scheduleDao.save(schedule);

		return schedule.getUuid();
	}

	@Override
	public void modifySchedule(HolidayScheduleDto scheduleDto) {
		logger.info("modifying {}", scheduleDto);

		HolidaySchedule schedule = scheduleDao.load(scheduleDto.getUuid());
		mapper.map(scheduleDto, schedule);
		fillEntityParkings(schedule, scheduleDto);

		scheduleDao.save(schedule);
	}

	@Override
	public void deleteSchedule(String scheduleUuid) {
		logger.info("deleting schedule={}", scheduleUuid);

		scheduleDao.delete(scheduleUuid);
	}

	@Override
	public HolidayScheduleDto findSchedule(String scheduleUuid) {
		logger.info("finding schedule={}", scheduleUuid);

		HolidaySchedule schedule = scheduleDao.find(scheduleUuid);
		if (schedule == null) {
			return null;
		}

		return mapper.map(schedule, HolidayScheduleDto.class);
	}

	@Override
	public List<HolidayScheduleDto> findAllSchedules() {
		logger.info("finding all schedules");

		List<HolidaySchedule> parkings = scheduleDao.findAll();
		return collectionMapper.mapToArrayList(parkings, HolidayScheduleDto.class);
	}

	private void fillEntityParkings(HolidaySchedule schedule, HolidayScheduleDto scheduleDto) {
		// dozer mapping from data transfer object to entity skips parking setting
		schedule.removeAllParkings();
		
		Set<ParkingDto> parkingDtoList = scheduleDto.getParkings();
		if (parkingDtoList != null) {
			for (ParkingDto parkingDto : parkingDtoList) {
				Parking parking = parkingDao.load(parkingDto.getUuid());
				schedule.addParking(parking);
			}
		}
	}

}
