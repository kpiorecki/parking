package com.kpiorecki.parking.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.exception.DomainException;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
public class BookingDao extends GenericDao<Long, Booking> {

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Inject
	private Logger logger;

	public BookingDao() {
		super(Booking.class);
	}

	public Booking load(String parkingUuid, LocalDate date) {
		logger.info("loading booking for parking={} and date={}", parkingUuid, dateFormatter.print(date));

		try {
			return createFindQuery(parkingUuid, date).getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			String warnMessage = String.format("booking for parking=%s and date=%s was not found", parkingUuid,
					dateFormatter.print(date));
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}
	}

	public Booking find(String parkingUuid, LocalDate date) {
		logger.info("finding booking by parking={} and date={}", parkingUuid, dateFormatter.print(date));

		List<Booking> results = createFindQuery(parkingUuid, date).getResultList();
		if (results.size() == 1) {
			return results.get(0);
		} else {
			return null;
		}
	}

	private TypedQuery<Booking> createFindQuery(String parkingUuid, LocalDate date) {
		TypedQuery<Booking> findQuery = entityManager.createNamedQuery("Booking.findByParkingAndDate", Booking.class);
		findQuery.setLockMode(LockModeType.OPTIMISTIC);
		findQuery.setParameter("parkingUuid", parkingUuid);
		findQuery.setParameter("date", date);

		return findQuery;
	}

}
