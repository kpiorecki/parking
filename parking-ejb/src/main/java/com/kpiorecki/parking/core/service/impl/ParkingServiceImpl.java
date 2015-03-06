package com.kpiorecki.parking.core.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.core.dto.ParkingDto;
import com.kpiorecki.parking.core.entity.Parking;
import com.kpiorecki.parking.core.entity.Parking_;
import com.kpiorecki.parking.core.service.ParkingService;

@Stateless
public class ParkingServiceImpl implements ParkingService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private EntityManager entityManager;

	@Inject
	private GenericDao genericDao;

	@Inject
	private UuidGenerator uuidGenerator;

	@Override
	public String addParking(ParkingDto parkingDto) {
		logger.info("adding parking {}", parkingDto);

		Parking parking = mapper.map(parkingDto, Parking.class);
		if (parking.getUuid() == null) {
			logger.info("generating uuid");
			String uuid = uuidGenerator.generateUuid();
			parking.setUuid(uuid);
		}

		entityManager.persist(parking);

		return parking.getUuid();
	}

	@Override
	public void modifyParking(ParkingDto parkingDto) {
		String message = String.format("modifying parking %s", parkingDto);
		logger.info(message);

		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingDto.getUuid());
		mapper.map(parkingDto, parking);

		entityManager.persist(parking);
	}

	@Override
	public void deleteParking(String parkingUuid) {
		String message = String.format("deleting parking with uuid=%s", parkingUuid);
		logger.info(message);

		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);

		entityManager.remove(parking);
	}

	@Override
	public ParkingDto findParking(String parkingUuid) {
		logger.info("finding parking with uuid={}", parkingUuid);

		Parking parking = genericDao.findEntity(Parking_.uuid, parkingUuid);
		if (parking == null) {
			return null;
		} else {
			return mapper.map(parking, ParkingDto.class);
		}
	}

	@Override
	public void addUser(String parkingUuid, String login) {
		// TODO Auto-generated method stub

	}
}
