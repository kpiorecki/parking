package com.kpiorecki.parking.ejb.service.parking.impl;

import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.math.DoubleMath;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.dto.RecordDto;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.service.parking.ParkingService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;
import com.kpiorecki.parking.ejb.util.DomainException;
import com.kpiorecki.parking.ejb.util.Role;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@Stateless
public class ParkingServiceImpl implements ParkingService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private CollectionMapper collectionMapper;

	@Inject
	private ParkingDao parkingDao;

	@Inject
	private UserDao userDao;

	@Inject
	private UuidGenerator uuidGenerator;

	@Override
	@RolesAllowed(Role.ADMIN)
	public String addParking(ParkingDto parkingDto) {
		logger.info("adding parking {}", parkingDto);

		Parking parking = mapper.map(parkingDto, Parking.class);
		if (parking.getUuid() == null) {
			String uuid = uuidGenerator.generateUuid();
			parking.setUuid(uuid);
		}

		parkingDao.save(parking);

		return parking.getUuid();
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void modifyParking(ParkingDto parkingDto) {
		logger.info("modifying parking {}", parkingDto);

		Parking parking = parkingDao.load(parkingDto.getUuid());
		mapper.map(parkingDto, parking);
		parkingDao.save(parking);
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void deleteParking(String parkingUuid) {
		logger.info("deleting parking={}", parkingUuid);

		parkingDao.delete(parkingUuid);
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void assignUser(String parkingUuid, String login, boolean vip) {
		logger.info("assigning user={}, vip={} to parking={}", login, vip, parkingUuid);

		User user = userDao.load(login);
		Parking parking = parkingDao.load(parkingUuid);

		Record record = new Record();
		record.setUser(user);
		record.setVip(vip);
		record.setPoints(calculateRecordPoints(parking));

		parking.addRecord(record);

		parkingDao.save(parking);
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void revokeUser(String parkingUuid, String login) {
		logger.info("revoking user={} from parking={}", login, parkingUuid);

		Parking parking = parkingDao.load(parkingUuid);
		Set<Record> records = parking.getRecords();
		for (Record record : records) {
			if (record.getUser().getLogin().equals(login)) {
				parking.removeRecord(record);
				parkingDao.save(parking);
				return;
			}
		}
		throw new DomainException(String.format("could not revoke user=%s from parking=%s - user record was not found",
				login, parkingUuid));
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void revokeAllUsers(String parkingUuid) {
		logger.info("revoking all users from parking={}", parkingUuid);

		Parking parking = parkingDao.load(parkingUuid);
		parking.removeAllRecords();
		parkingDao.save(parking);
	}

	@Override
	public ParkingDto findParking(String parkingUuid) {
		logger.info("finding parking={}", parkingUuid);

		Parking parking = parkingDao.find(parkingUuid);
		if (parking == null) {
			return null;
		}

		return mapper.map(parking, ParkingDto.class);
	}

	@Override
	public List<ParkingDto> findUserParkings(String login) {
		logger.info("finding parkings for user={}", login);

		List<Parking> userParkings = parkingDao.findUserParkings(login);
		return collectionMapper.mapToArrayList(userParkings, ParkingDto.class);
	}

	@Override
	public List<ParkingDto> findAllParkings() {
		logger.info("finding all parkings");

		List<Parking> parkings = parkingDao.findAll();
		return collectionMapper.mapToArrayList(parkings, ParkingDto.class);
	}

	@Override
	public List<RecordDto> findRecords(String parkingUuid) {
		logger.info("finding records for parking={}", parkingUuid);

		Parking parking = parkingDao.load(parkingUuid);
		Set<Record> records = parking.getRecords();
		return collectionMapper.mapToArrayList(records, RecordDto.class);
	}

	private int calculateRecordPoints(Parking parking) {
		int recordPoints = 0;
		Set<Record> records = parking.getRecords();
		if (!records.isEmpty()) {
			/**
			 * calculate new record's points as mean value of existing records points (rounded upwards)
			 */
			Function<Record, Integer> getPointsFunction = new Function<Record, Integer>() {

				@Override
				public Integer apply(Record record) {
					return record.getPoints();
				}
			};
			Iterator<Integer> pointsIterator = Iterators.transform(records.iterator(), getPointsFunction);
			double pointsMean = DoubleMath.mean(pointsIterator);
			recordPoints = DoubleMath.roundToInt(pointsMean, RoundingMode.CEILING);
		}
		return recordPoints;
	}

}
