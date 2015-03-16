package com.kpiorecki.parking.ejb.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Parking_;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.User_;
import com.kpiorecki.parking.ejb.exception.DomainException;
import com.kpiorecki.parking.ejb.jpa.GenericDao;
import com.kpiorecki.parking.ejb.service.BookingService;
import com.kpiorecki.parking.ejb.service.ParkingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
@Transactional
public class BookingServiceImpl implements BookingService {

	@Inject
	private Logger logger;

	@Inject
	private EntityManager entityManager;

	@Inject
	private GenericDao genericDao;

	@Inject
	private ParkingService parkingService;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Override
	public void book(String parkingUuid, String login, LocalDate date) {
		String message = String.format("adding booking entry to parking with uuid=%s for user=%s and date=%s",
				parkingUuid, login, dateFormatter.print(date));
		logger.info(message);

		boolean userAssigned = parkingService.isUserAssigned(parkingUuid, login);
		if (!userAssigned) {
			String warnMessage = String.format("%s - user is not assigned to parking", message);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}

		Booking booking = findBooking(parkingUuid, date);
		if (booking == null) {
			logger.info("{} - booking was not found - creating new one", message);

			Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);
			booking = new Booking();
			booking.setDate(date);
			booking.setParking(parking);
			booking.setEntries(new HashSet<BookingEntry>());

			entityManager.persist(booking);
		}

		User user = genericDao.findExistingEntity(User_.login, login);
		BookingEntry entry = new BookingEntry();
		entry.setUser(user);
		entityManager.persist(entry);

		booking.getEntries().add(entry);
		entityManager.persist(booking);
	}

	@Override
	public void cancel(String parkingUuid, String login, LocalDate date) {
		String message = String.format("removing booking entry from parking with uuid=%s for user=%s and date=%s",
				parkingUuid, login, dateFormatter.print(date));
		logger.info(message);

		Booking booking = findBooking(parkingUuid, date);
		if (booking == null) {
			String warnMessage = String.format("%s - booking was not found", message);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}
		Set<BookingEntry> entries = booking.getEntries();
		for (BookingEntry entry : entries) {
			if (entry.getUser().getLogin().equals(login)) {
				entries.remove(entry);
				entityManager.remove(entry);

				logger.info("{} - removed", message);
				return;
			}
		}

		String warnMessage = String.format("%s - did not find entry", message);
		logger.warn(warnMessage);
		throw new DomainException(warnMessage);
	}

	private Booking findBooking(String parkingUuid, LocalDate date) {
		String message = String.format("finding booking by parking with uuid=%s and date=%s", parkingUuid,
				dateFormatter.print(date));
		logger.info(message);

		Parking parking = genericDao.findExistingEntity(Parking_.uuid, parkingUuid);

		TypedQuery<Booking> findQuery = entityManager.createNamedQuery("Booking.findByParkingAndDate", Booking.class);
		findQuery.setParameter("parkingId", parking.getId());
		findQuery.setParameter("date", date);
		List<Booking> results = findQuery.getResultList();

		if (results.size() == 1) {
			logger.info("{} - found", message);
			return results.get(0);
		} else {
			logger.info("{} - not found", message);
			return null;
		}
	}
}
