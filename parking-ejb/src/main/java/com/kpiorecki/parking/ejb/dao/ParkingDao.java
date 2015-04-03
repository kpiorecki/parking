package com.kpiorecki.parking.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;

@Stateless
public class ParkingDao extends GenericDao<String, Parking> {

	@Inject
	private Logger logger;

	@Inject
	private BookingDao bookingDao;

	public ParkingDao() {
		super(Parking.class);
	}

	public boolean isUserAssigned(String parkingUuid, String login) {
		logger.info("checking if user={} is assigned to parking={}", login, parkingUuid);

		TypedQuery<Record> query = entityManager.createNamedQuery("Record.findRecordByUserAndParking", Record.class);
		query.setParameter("login", login);
		query.setParameter("parkingUuid", parkingUuid);
		query.setLockMode(LockModeType.OPTIMISTIC);

		List<Record> records = query.getResultList();
		return !records.isEmpty();
	}

	@Override
	public void delete(String id) {
		logger.info("deleting parking={} bookings", id);

		TypedQuery<Long> query = entityManager.createNamedQuery("Booking.findIdsByParking", Long.class);
		query.setParameter("parkingUuid", id);
		List<Long> bookingsIds = query.getResultList();
		for (Long bookingId : bookingsIds) {
			bookingDao.delete(bookingId);
		}

		super.delete(id);
	}

}
