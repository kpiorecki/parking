package com.kpiorecki.parking.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;

@Stateless
public class UserDao extends ArchivableDao<String, User> {

	@Inject
	private Logger logger;

	@Inject
	private ParkingDao parkingDao;

	public UserDao() {
		super(User.class);
	}

	@Override
	public void delete(String id) {
		super.delete(id);

		TypedQuery<Record> query = entityManager.createNamedQuery("Record.findRecordsByUser", Record.class);
		query.setParameter("login", id);
		query.setLockMode(LockModeType.OPTIMISTIC);
		List<Record> records = query.getResultList();

		for (Record record : records) {
			Parking parking = record.getParking();
			logger.info("deleting user={} record from parking={}", id, parking.getUuid());
			parking.removeRecord(record);

			parkingDao.save(parking);
		}

	}
}
