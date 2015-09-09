package com.kpiorecki.parking.ejb.service.booking.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.BookingDto;
import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.service.booking.BookingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.DomainException;
import com.kpiorecki.parking.ejb.util.Role;

@Stateless
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
	private BookingScheduler scheduler;

	@Inject
	private Mapper mapper;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	// TODO add logic (config property) to set deadlines for changing booking status
	
	@Override
	@RolesAllowed(Role.USER)
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

		// TODO logic for setting status in new Booking
		validateStatus(booking, EnumSet.of(BookingStatus.DRAFT, BookingStatus.RELEASED));

		User user = userDao.load(login);
		BookingEntry entry = new BookingEntry();
		entry.setUser(user);
		booking.addEntry(entry);

		scheduler.updateSchedule(booking);
		bookingDao.save(booking);
	}

	@Override
	@RolesAllowed(Role.USER)
	public void cancel(String parkingUuid, String login, LocalDate date) {
		String message = String.format("removing booking entry from parking=%s for user=%s and date=%s", parkingUuid,
				login, dateFormatter.print(date));
		logger.info(message);

		Booking booking = bookingDao.load(parkingUuid, date);
		validateStatus(booking, EnumSet.of(BookingStatus.DRAFT, BookingStatus.RELEASED));

		for (BookingEntry entry : booking.getEntries()) {
			if (entry.getUser().getLogin().equals(login)) {
				booking.removeEntry(entry);

				scheduler.updateSchedule(booking);
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

		validateStatus(booking, EnumSet.of(BookingStatus.DRAFT));
		booking.setStatus(BookingStatus.RELEASED);

		scheduler.updateSchedule(booking);
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

		validateStatus(booking, EnumSet.of(BookingStatus.DRAFT, BookingStatus.RELEASED));
		booking.setStatus(BookingStatus.LOCKED);

		scheduler.lockSchedule(booking);
		bookingDao.save(booking);
		parkingDao.save(booking.getParking());
	}

	@Override
	@RolesAllowed(Role.USER)
	public List<ParkingBookingDto> findUserBookings(String login, LocalDate startDate, LocalDate endDate) {
		logger.info("finding bookings for user={}, startDate={}, endDate={}", login, dateFormatter.print(startDate),
				dateFormatter.print(endDate));

		// find parking entities that the user is assigned to
		List<Parking> parkings = parkingDao.findUserParkings(login);
		Collections.sort(parkings, new ParkingComparator());

		// find bookings for given parkings and date range
		List<Booking> bookings = bookingDao.findBookings(startDate, endDate, parkings);

		// initialize list of parking bookings data transfer objects and helper map
		List<ParkingBookingDto> parkingBookings = new ArrayList<ParkingBookingDto>(parkings.size());
		Map<String, ParkingBookingDto> helperMap = new HashMap<>(parkings.size());
		for (Parking parking : parkings) {
			ParkingBookingDto parkingBooking = new ParkingBookingDto();
			parkingBooking.setParking(mapper.map(parking, ParkingDto.class));
			parkingBooking.setBookingList(new ArrayList<BookingDto>());

			parkingBookings.add(parkingBooking);
			helperMap.put(parking.getUuid(), parkingBooking);
		}

		// split bookings into appropriate parkingBookings
		for (Booking booking : bookings) {
			String parkingUuid = booking.getParking().getUuid();
			ParkingBookingDto parkingBooking = helperMap.get(parkingUuid);
			if (parkingBooking == null) {
				String error = String.format("could not find parkingBooking for parking=%s", parkingUuid);
				logger.error(error);
				throw new DomainException(error);
			}

			BookingDto bookingDto = mapper.map(booking, BookingDto.class);
			parkingBooking.getBookingList().add(bookingDto);
		}
		
		// TODO return always bookings for each day (even if not created yet) with properly set status
		// TODO add business holiday UC

		return parkingBookings;
	}

	private void validateStatus(Booking booking, Set<BookingStatus> allowedStatuses) {
		BookingStatus bookingStatus = booking.getStatus();
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
