package com.kpiorecki.parking.ejb.service.booking.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.dao.ParkingDao;
import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.BookingDto;
import com.kpiorecki.parking.ejb.dto.BookingEntryDto;
import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule.DateStatus;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Parking_;
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
	private BookingStatusPolicy statusPolicy;

	@Inject
	private Mapper mapper;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

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
			booking = createNewBooking(parkingUuid, date);
		}

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

		for (BookingEntry entry : booking.getEntries()) {
			if (entry.getUser().getLogin().equals(login)) {
				booking.removeEntry(entry);

				scheduler.updateSchedule(booking);
				bookingDao.save(booking);
				return;
			}
		}

		throw new DomainException(String.format("%s - did not find entry", message));
	}

	@Override
	public void update(String parkingUuid, String login, Collection<LocalDate> bookedDates,
			Collection<LocalDate> cancelledDates) {
		logger.info("updating parking={} for user={} with {} booked and {} cancelled dates", parkingUuid, login,
				bookedDates.size(), cancelledDates.size());
		for (LocalDate date : bookedDates) {
			book(parkingUuid, login, date);
		}
		for (LocalDate date : cancelledDates) {
			cancel(parkingUuid, login, date);
		}
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

		booking.release();

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

		booking.lock();

		scheduler.lockSchedule(booking);
		bookingDao.save(booking);
		parkingDao.save(booking.getParking());
	}

	@Override
	@RolesAllowed(Role.USER)
	public ParkingBookingDto findBookings(String parkingName, String login, LocalDate startDate, LocalDate endDate) {
		logger.info("finding bookings for parkingName={}, user={}, startDate={}, endDate={}", parkingName, login,
				dateFormatter.print(startDate), dateFormatter.print(endDate));

		Parking parking = parkingDao.load(Parking_.name, parkingName);

		validateBookingDates(startDate, endDate);
		validateUser(parking.getUuid(), login);

		List<Parking> parkings = Collections.singletonList(parking);
		List<ParkingBookingDto> parkingBookings = findParkingBookings(startDate, endDate, parkings);

		return parkingBookings.get(0);
	}

	@Override
	@RolesAllowed(Role.USER)
	public List<ParkingBookingDto> findAllBookings(String login, LocalDate startDate, LocalDate endDate) {
		logger.info("finding all bookings for user={}, startDate={}, endDate={}", login,
				dateFormatter.print(startDate), dateFormatter.print(endDate));

		validateBookingDates(startDate, endDate);

		List<Parking> parkings = parkingDao.findUserParkings(login);
		Collections.sort(parkings, new ParkingComparator());

		return findParkingBookings(startDate, endDate, parkings);
	}

	private List<ParkingBookingDto> findParkingBookings(LocalDate startDate, LocalDate endDate, List<Parking> parkings) {
		// find bookings for given parking list and date range
		List<Booking> bookings = bookingDao.findBookings(startDate, endDate, parkings);

		// number of days with bookingDtos returned in each parking
		final int daysNumber = Days.daysBetween(startDate, endDate).getDays();

		// initialize list of parking bookings data transfer objects and helper parking map
		List<ParkingBookingDto> parkingBookings = new ArrayList<>(parkings.size());
		Map<String, ParkingBookingDto> parkingMap = new HashMap<>(parkings.size());
		for (Parking parking : parkings) {
			ParkingBookingDto parkingBookingDto = createParkingBookingDto(parking, daysNumber);

			parkingBookings.add(parkingBookingDto);
			parkingMap.put(parking.getUuid(), parkingBookingDto);
		}

		// split bookings into appropriate parkingBookings
		for (Booking booking : bookings) {
			addBooking(booking, parkingMap, startDate);
		}

		// add empty bookings for days that have not been booked yet
		addEmptyBookings(startDate, parkingBookings);

		// add holiday information
		for (Parking parking : parkings) {
			ParkingBookingDto parkingBookingDto = parkingMap.get(parking.getUuid());
			addHolidayInfo(parkingBookingDto, parking.getHolidaySchedule());
		}

		return parkingBookings;
	}

	private void addBooking(Booking booking, Map<String, ParkingBookingDto> parkingMap, LocalDate startDate) {
		String parkingUuid = booking.getParking().getUuid();
		ParkingBookingDto parkingBooking = parkingMap.get(parkingUuid);
		if (parkingBooking == null) {
			throw new DomainException(String.format("could not find parkingBooking for parking=%s", parkingUuid));
		}

		BookingDto bookingDto = mapper.map(booking, BookingDto.class);
		int index = Days.daysBetween(startDate, booking.getDate()).getDays();
		parkingBooking.getBookingList().set(index, bookingDto);
	}

	private void addEmptyBookings(LocalDate startDate, List<ParkingBookingDto> parkingBookings) {
		final DateTime now = new DateTime();
		for (ParkingBookingDto parkingBooking : parkingBookings) {
			ListIterator<BookingDto> iterator = parkingBooking.getBookingList().listIterator();
			LocalDate currentDate = startDate;
			while (iterator.hasNext()) {
				BookingDto bookingDto = iterator.next();
				if (bookingDto == null) {
					// add empty booking for that date
					iterator.set(createEmptyBookingDto(currentDate, now));
				}
				currentDate = currentDate.plusDays(1);
			}
		}
	}

	private ParkingBookingDto createParkingBookingDto(Parking parking, int daysNumber) {
		ParkingDto parkingDto = mapper.map(parking, ParkingDto.class);

		// create fixed size list for all BookingDto objects and fill it with null values
		List<BookingDto> bookingList = Arrays.asList(new BookingDto[daysNumber]);

		ParkingBookingDto parkingBooking = new ParkingBookingDto();
		parkingBooking.setParking(parkingDto);
		parkingBooking.setBookingList(bookingList);

		return parkingBooking;
	}

	private BookingDto createEmptyBookingDto(LocalDate date, DateTime now) {
		BookingDto bookingDto = new BookingDto();

		bookingDto.setDate(date);
		bookingDto.setEntries(new HashSet<BookingEntryDto>());
		bookingDto.setStatus(statusPolicy.getDefaultStatus(date, now));

		return bookingDto;
	}

	private Booking createNewBooking(String parkingUuid, LocalDate date) {
		Parking parking = parkingDao.load(parkingUuid);

		Booking booking = new Booking();
		booking.setDate(date);
		booking.setParking(parking);
		booking.updateStatus(statusPolicy.getDefaultStatus(date, new DateTime()));

		return booking;
	}

	private void addHolidayInfo(ParkingBookingDto parkingBooking, HolidaySchedule schedule) {
		for (BookingDto bookingDto : parkingBooking.getBookingList()) {
			if (schedule == null) {
				bookingDto.setHoliday(false);
				bookingDto.setNotes(Collections.emptyList());
			} else {
				DateStatus dateStatus = schedule.getDateStatus(bookingDto.getDate());
				bookingDto.setHoliday(dateStatus.isHoliday());
				bookingDto.setNotes(dateStatus.getNotes());
			}
		}
	}

	private void validateUser(String parkingUuid, String login) {
		boolean userAssigned = parkingDao.isUserAssigned(parkingUuid, login);
		if (!userAssigned) {
			throw new DomainException(String.format("user=%s is not assigned to parking=%s", login, parkingUuid));
		}
	}

	private void validateBookingDates(LocalDate startDate, LocalDate endDate) {
		if (!endDate.isAfter(startDate)) {
			throw new DomainException(String.format("booking endDate=%s is not after startDate=%s",
					dateFormatter.print(endDate), dateFormatter.print(startDate)));
		}
	}

}
