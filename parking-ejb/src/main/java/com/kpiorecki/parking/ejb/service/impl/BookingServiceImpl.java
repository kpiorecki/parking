package com.kpiorecki.parking.ejb.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.Booking.Status;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.exception.DomainException;
import com.kpiorecki.parking.ejb.service.BookingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
@Transactional
public class BookingServiceImpl implements BookingService {

	@Inject
	private Logger logger;

	@Inject
	private ParkingDao parkingDao;

	@Inject
	private BookingDao bookingDao;

	@Inject
	private UserDao userDao;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Override
	public void book(String parkingUuid, String login, LocalDate date) {
		String message = String.format("adding booking entry to parking=%s for user=%s and date=%s", parkingUuid,
				login, dateFormatter.print(date));
		logger.info(message);

		boolean userAssigned = parkingDao.isUserAssigned(parkingUuid, login);
		if (!userAssigned) {
			String warnMessage = String.format("%s - user is not assigned to parking", message);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}

		Booking booking = bookingDao.findBooking(parkingUuid, date);
		if (booking == null) {
			logger.info("{} - booking was not found - creating new one", message);

			Parking parking = parkingDao.load(parkingUuid);
			booking = new Booking();
			booking.setDate(date);
			booking.setParking(parking);
		}

		validateStatus(booking, message);

		User user = userDao.load(login);
		BookingEntry entry = new BookingEntry();
		entry.setUser(user);
		booking.addEntry(entry);

		bookingDao.save(booking);
	}

	@Override
	public void cancel(String parkingUuid, String login, LocalDate date) {
		String message = String.format("removing booking entry from parking=%s for user=%s and date=%s", parkingUuid,
				login, dateFormatter.print(date));
		logger.info(message);

		Booking booking = bookingDao.findBooking(parkingUuid, date);
		if (booking == null) {
			String warnMessage = String.format("%s - booking was not found", message);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}

		validateStatus(booking, message);

		for (BookingEntry entry : booking.getEntries()) {
			if (entry.getUser().getLogin().equals(login)) {
				booking.removeEntry(entry);
				bookingDao.save(booking);
				return;
			}
		}

		String warnMessage = String.format("%s - did not find entry", message);
		logger.warn(warnMessage);
		throw new DomainException(warnMessage);
	}

	private void validateStatus(Booking booking, String message) {
		if (booking.getStatus() == Status.LOCKED) {
			String warnMessage = String.format("%s - booking is locked", message);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}
	}

}
