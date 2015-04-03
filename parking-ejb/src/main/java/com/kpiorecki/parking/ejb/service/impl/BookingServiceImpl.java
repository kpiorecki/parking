package com.kpiorecki.parking.ejb.service.impl;

import java.util.EnumSet;
import java.util.Set;

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

		validateUser(parkingUuid, login);

		Booking booking = bookingDao.find(parkingUuid, date);
		if (booking == null) {
			logger.info("{} - booking was not found - creating new one", message);

			Parking parking = parkingDao.load(parkingUuid);
			booking = new Booking();
			booking.setDate(date);
			booking.setParking(parking);
		}

		validateStatus(booking, EnumSet.of(Status.DRAFT, Status.RELEASED));

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

		Booking booking = bookingDao.load(parkingUuid, date);
		validateStatus(booking, EnumSet.of(Status.DRAFT, Status.RELEASED));

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

	@Override
	public void release(String parkingUuid, LocalDate date) {
		String message = String.format("releasing booking entries from parking=%s and date=%s", parkingUuid,
				dateFormatter.print(date));
		logger.info(message);

		Booking booking = bookingDao.find(parkingUuid, date);
		if (booking == null) {
			logger.info("{} - skipped (booking not found)", message);
			return;
		}

		validateStatus(booking, EnumSet.of(Status.DRAFT));

		booking.setStatus(Status.RELEASED);
		bookingDao.save(booking);
	}

	@Override
	public void lock(String parkingUuid, LocalDate date) {
		String message = String.format("locking booking entries from parking=%s and date=%s", parkingUuid,
				dateFormatter.print(date));
		logger.info(message);

		Booking booking = bookingDao.find(parkingUuid, date);
		if (booking == null) {
			logger.info("{} - skipped (booking not found)", message);
			return;
		}

		validateStatus(booking, EnumSet.of(Status.DRAFT, Status.RELEASED));

		booking.setStatus(Status.LOCKED);
		bookingDao.save(booking);
	}

	private void validateStatus(Booking booking, Set<Status> allowedStatuses) {
		Status bookingStatus = booking.getStatus();
		if (!allowedStatuses.contains(bookingStatus)) {
			String warnMessage = String.format("invalid booking status %s", bookingStatus);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}
	}

	private void validateUser(String parkingUuid, String login) {
		boolean userAssigned = parkingDao.isUserAssigned(parkingUuid, login);
		if (!userAssigned) {
			String warnMessage = String.format("user=%s is not assigned to parking=%s", login, parkingUuid);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}
	}

}
