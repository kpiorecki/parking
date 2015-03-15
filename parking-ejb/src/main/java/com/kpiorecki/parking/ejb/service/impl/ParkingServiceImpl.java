package com.kpiorecki.parking.ejb.service.impl;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.dto.RecordDto;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Parking_;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.User_;
import com.kpiorecki.parking.ejb.exception.DomainException;
import com.kpiorecki.parking.ejb.jpa.GenericDao;
import com.kpiorecki.parking.ejb.service.ParkingService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@Stateless
@Transactional
public class ParkingServiceImpl implements ParkingService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private CollectionMapper collectionMapper;

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
	public void assignUser(String parkingUuid, String login, boolean vip) {
		logger.info("assigning user={}, vip={} to parking with uuid={}", login, vip, parkingUuid);

		User user = genericDao.findExistingEntity(User_.login, login);
		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);

		Record record = new Record();
		record.setUser(user);
		record.setVip(vip);
		record.setPoints(0);

		parking.getRecords().add(record);

		entityManager.persist(parking);
	}

	@Override
	public void revokeUser(String parkingUuid, String login) {
		logger.info("revoking user={} from parking with uuid={}", login, parkingUuid);

		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);
		Record record = getUserRecord(parking, login);
		if (record != null) {
			parking.getRecords().remove(record);
			entityManager.remove(record);
			return;
		}
		String message = String.format(
				"could not revoke user=%s from parking with uuid=%s - user record was not found", login, parkingUuid);
		logger.warn(message);
		throw new DomainException(message);
	}

	@Override
	public void revokeAllUsers(String parkingUuid) {
		logger.info("revoking all users from parking with uuid={}", parkingUuid);

		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);
		parking.getRecords().clear();

		entityManager.persist(parking);
	}

	@Override
	public boolean isUserAssigned(String parkingUuid, String login) {
		logger.info("checking if user={} is assigned to parking with uuid={}", login, parkingUuid);

		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);
		Record record = getUserRecord(parking, login);

		return record != null;
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
	public List<ParkingDto> findAllParkings() {
		logger.info("finding all parkings");

		List<Parking> parkings = genericDao.findAllEntities(Parking.class);

		return collectionMapper.mapToArrayList(parkings, ParkingDto.class);
	}

	@Override
	public List<RecordDto> findRecords(String parkingUuid) {
		logger.info("finding records for parking with uuid={}", parkingUuid);

		Parking parking = genericDao.findEntity(Parking_.uuid, parkingUuid);
		if (parking == null) {
			return null;
		} else {
			Set<Record> records = parking.getRecords();
			return collectionMapper.mapToArrayList(records, RecordDto.class);
		}
	}

	private Record getUserRecord(Parking parking, String login) {
		Set<Record> records = parking.getRecords();
		for (Record record : records) {
			if (record.getUser().getLogin().equals(login)) {
				return record;
			}
		}
		return null;
	}

}
