package com.kpiorecki.parking.ejb.service.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.GreenMailIT;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.dto.BookingDto;
import com.kpiorecki.parking.ejb.dto.BookingEntryDto;
import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class BookingServiceIT extends GreenMailIT {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createFullDeployment();
	}

	@Inject
	private BookingService bookingService;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	private EntityManager entityManager;

	@Inject
	private BookingDao bookingDao;

	@Test
	public void shouldAcceptParkingBooking() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		entityManager.flush();

		// when
		LocalDate date = getBookingDate();
		bookingService.book(parkingUuid, "login1", date);

		// then
		Booking booking = bookingDao.find(parkingUuid, date);
		assertEquals(parkingUuid, booking.getParking().getUuid());

		Set<BookingEntry> acceptedEntries = booking.getAcceptedEntries();
		assertEquals(1, acceptedEntries.size());

		BookingEntry entry = acceptedEntries.iterator().next();
		assertEquals("login1", entry.getUser().getLogin());
	}

	@Test(expected = Exception.class)
	public void shouldNotBookParkingForUnassignedUser() {
		// given
		User user1 = testUtilities.persistUser("login1");
		String parkingUuid = testUtilities.persistParking(user1).getUuid();
		testUtilities.persistUser("login2");
		entityManager.flush();

		// when
		LocalDate date = getBookingDate();
		bookingService.book(parkingUuid, "login2", date);

		// then exception should be thrown - user is not assigned to parking
	}

	@Test(expected = Exception.class)
	public void shouldNotBookParkingTwice() {
		// given
		User user = testUtilities.persistUser("login");
		String parkingUuid = testUtilities.persistParking(user).getUuid();
		entityManager.flush();

		// when
		LocalDate date = getBookingDate();
		bookingService.book(parkingUuid, "login", date);
		bookingService.book(parkingUuid, "login", date);
		entityManager.flush();

		// then exception should be thrown - the user is already booked
	}

	@Test
	public void shouldCancelAcceptedBooking() {
		// given
		LocalDate date = getBookingDate();
		User user1 = testUtilities.persistUser("login1");
		Parking parking = testUtilities.persistParking(user1);
		Booking booking = testUtilities.createBooking(parking, date, user1);
		booking.acceptEntries(booking.getEntries());

		entityManager.persist(booking);
		entityManager.flush();

		// when
		String parkingUuid = parking.getUuid();
		bookingService.cancel(parkingUuid, "login1", date);

		// then
		Booking foundBooking = bookingDao.find(parkingUuid, date);
		assertEquals(parkingUuid, foundBooking.getParking().getUuid());

		assertTrue(foundBooking.getEntries().isEmpty());
	}

	@Test(expected = Exception.class)
	public void shouldNotCancelNonExistingBooking() {
		// given
		LocalDate date = getBookingDate();
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		Parking parking = testUtilities.persistParking(user1, user2);
		testUtilities.persistBooking(parking, date, user1);
		entityManager.flush();

		// when
		String parkingUuid = parking.getUuid();
		bookingService.cancel(parkingUuid, "login2", date);

		// then exception should be thrown - booking does not exist
	}

	@Test
	public void shouldReleaseBooking() {
		// given
		LocalDate date = getBookingDate();
		User user = testUtilities.persistUser("login");
		Parking parking = testUtilities.persistParking(user);
		testUtilities.persistBooking(parking, date, user);
		entityManager.flush();

		// when
		String parkingUuid = parking.getUuid();
		bookingService.release(parkingUuid, date);

		// then
		Booking booking = bookingDao.find(parkingUuid, date);
		assertEquals(BookingStatus.RELEASED, booking.getStatus());
	}

	@Test(expected = Exception.class)
	public void shouldNotReleaseBookingTwice() {
		// given
		LocalDate date = getBookingDate();
		User user = testUtilities.persistUser("login");
		Parking parking = testUtilities.persistParking(user);
		testUtilities.persistBooking(parking, date, user);
		entityManager.flush();

		// when
		String parkingUuid = parking.getUuid();
		bookingService.release(parkingUuid, date);
		bookingService.release(parkingUuid, date);

		// then exception should be thrown - booking already released
	}

	@Test
	public void shouldLockBooking() {
		// given
		LocalDate date = getBookingDate();
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		Parking parking = testUtilities.persistParking(user1, user2);
		testUtilities.persistBooking(parking, date, user1, user2);
		entityManager.flush();

		// when
		String parkingUuid = parking.getUuid();
		bookingService.lock(parkingUuid, date);

		// then
		Booking booking = bookingDao.find(parkingUuid, date);
		assertEquals(BookingStatus.LOCKED, booking.getStatus());

		Set<Record> records = parking.getRecords();
		for (Record record : records) {
			assertEquals(Integer.valueOf(1), record.getPoints());
		}
	}

	@Test
	public void shouldLockBookingsAccordingToPolicy() {
		// given
		LocalDate today = new LocalDate();
		LocalDate nextWeek = today.plusWeeks(1);

		LocalDate bookingDate1 = today.minusDays(1);
		LocalDate bookingDate2 = today.minusDays(2);

		User user1 = testUtilities.persistUser("login1");
		Parking parking1 = testUtilities.persistParking(user1);
		Parking parking2 = testUtilities.persistParking(user1);

		Booking booking1 = testUtilities.createBooking(parking1, bookingDate1, user1);
		booking1.acceptEntries(booking1.getEntries());

		Booking booking2 = testUtilities.createBooking(parking2, bookingDate2, user1);
		booking2.acceptEntries(booking2.getEntries());

		Booking nextWeekBooking = testUtilities.createBooking(parking1, nextWeek, user1);
		nextWeekBooking.acceptEntries(nextWeekBooking.getEntries());
		nextWeekBooking.release();

		entityManager.persist(booking1);
		entityManager.persist(booking2);
		entityManager.persist(nextWeekBooking);
		entityManager.flush();

		// when
		bookingService.lockAccordingToPolicy();

		// then
		validateBookingStatus(parking1.getUuid(), bookingDate1, BookingStatus.LOCKED);
		validateBookingStatus(parking2.getUuid(), bookingDate2, BookingStatus.LOCKED);
		validateBookingStatus(parking1.getUuid(), nextWeek, BookingStatus.RELEASED);
	}

	@Test(expected = Exception.class)
	public void shouldNotFindBookings() {
		// given
		Parking parking = testUtilities.persistParking();
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		LocalDate startDate = getBookingDate();
		LocalDate endDate = startDate.plusWeeks(1);
		bookingService.findBookings(parking.getName(), "login", startDate, endDate);

		// then exception should be thrown - user is not assigned to given parking
	}

	@Test
	public void shouldNotFindAnyBookings() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		LocalDate startDate = getBookingDate();
		LocalDate endDate = startDate.plusWeeks(1);
		List<ParkingBookingDto> bookings = bookingService.findAllBookings("login", startDate, endDate);

		// then
		assertNotNull(bookings);
		assertTrue(bookings.isEmpty());
	}

	@Test(expected = Exception.class)
	public void shouldNotUpdateBookings() {
		// given
		Set<LocalDate> bookedDates = new HashSet<>();
		bookedDates.add(new LocalDate());
		bookedDates.add(new LocalDate().plusDays(1));

		Set<LocalDate> cancelledDates = new HashSet<>();
		cancelledDates.add(new LocalDate());
		cancelledDates.add(new LocalDate().minusDays(1));
		cancelledDates.add(new LocalDate().minusDays(2));
		cancelledDates.add(new LocalDate().minusDays(3));

		// when
		bookingService.update("parkingUuid", "login", bookedDates, cancelledDates);

		// then exception should be thrown
	}

	@Test
	public void shouldFindAllBookings() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		User user3 = testUtilities.persistUser("login3");

		Parking parking1 = testUtilities.createParking(user1, user2, user3);
		parking1.setName("p1");
		entityManager.persist(parking1);

		Parking parking2 = testUtilities.createParking(user1, user2, user3);
		parking2.setName("p2");
		entityManager.persist(parking2);

		Parking parking3 = testUtilities.createParking(user3);
		parking3.setName("p3");
		entityManager.persist(parking3);

		LocalDate startDate = getBookingDate();
		LocalDate endDate = startDate.plusWeeks(1);
		LocalDate cursorDate = startDate;
		while (cursorDate.isBefore(endDate)) {
			testUtilities.persistBooking(parking1, cursorDate, user1);
			testUtilities.persistBooking(parking2, cursorDate, user1, user2);
			testUtilities.persistBooking(parking3, cursorDate, user3);
			cursorDate = cursorDate.plusDays(1);
		}
		entityManager.flush();

		// when
		List<ParkingBookingDto> bookings = bookingService.findAllBookings("login1", startDate, endDate);

		// then
		assertNotNull(bookings);
		assertEquals(2, bookings.size());

		ParkingBookingDto parkingBooking1 = bookings.get(0);
		validateParkingBooking(parkingBooking1, startDate, endDate, "p1", "login1");

		ParkingBookingDto parkingBooking2 = bookings.get(1);
		validateParkingBooking(parkingBooking2, startDate, endDate, "p2", "login1", "login2");
	}

	private void validateParkingBooking(ParkingBookingDto parkingBooking, LocalDate startDate, LocalDate endDate,
			String parkingName, String... logins) {
		assertEquals(parkingName, parkingBooking.getParking().getName());

		List<BookingDto> list = parkingBooking.getBookingList();
		int daysNumber = Days.daysBetween(startDate, endDate).getDays();

		assertEquals(daysNumber, list.size());
		assertTrue(list.get(0).getDate().isEqual(startDate));
		assertTrue(list.get(list.size() - 1).getDate().isEqual(endDate.minusDays(1)));

		Set<BookingEntryDto> entries = list.get(0).getEntries();
		assertEquals(logins.length, entries.size());
		for (String login : logins) {
			assertTrue(entries.stream().anyMatch(e -> login.equals(e.getLogin())));
		}
	}

	private void validateBookingStatus(String parkingUuid, LocalDate date, BookingStatus status) {
		Booking booking = bookingDao.find(parkingUuid, date);
		assertNotNull(booking);
		assertEquals(status, booking.getStatus());
	}

	private LocalDate getBookingDate() {
		return new DateTime().toLocalDate().plusWeeks(2);
	}
}
