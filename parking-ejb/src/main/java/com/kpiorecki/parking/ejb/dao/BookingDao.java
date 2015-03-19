package com.kpiorecki.parking.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Booking;
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

	public Booking findBooking(String parkingUuid, LocalDate date) {
		logger.info("finding booking by parking={} and date={}", parkingUuid, dateFormatter.print(date));

		TypedQuery<Booking> findQuery = entityManager.createNamedQuery("Booking.findByParkingAndDate", Booking.class);
		findQuery.setLockMode(LockModeType.OPTIMISTIC);
		findQuery.setParameter("parkingUuid", parkingUuid);
		findQuery.setParameter("date", date);
		List<Booking> results = findQuery.getResultList();

		if (results.size() == 1) {
			return results.get(0);
		} else {
			return null;
		}
	}

}
