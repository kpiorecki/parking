package com.kpiorecki.parking.ejb.service.booking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import com.kpiorecki.parking.ejb.GreenMailTest;
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
public class BookingServiceTest extends GreenMailTest {

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
	public void shouldBookParking() {
		// given
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		String parkingUuid = testUtilities.persistParking(user1, user2).getUuid();
		entityManager.flush();

		// when
		LocalDate date = new DateTime().toLocalDate().plusWeeks(1);
		bookingService.book(parkingUuid, "login1", date);

		// then
		Booking booking = bookingDao.find(parkingUuid, date);
		assertEquals(parkingUuid, booking.getParking().getUuid());

		Set<BookingEntry> acceptedEntries = booking.getAcceptedEntries();
		assertEquals(1, acceptedEntries.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotBookParkingForUnassignedUser() {
		// given
		User user1 = testUtilities.persistUser("login1");
		String parkingUuid = testUtilities.persistParking(user1).getUuid();
		testUtilities.persistUser("login2");
		entityManager.flush();

		// when
		LocalDate date = new DateTime().toLocalDate();
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
		LocalDate date = new DateTime().toLocalDate();
		bookingService.book(parkingUuid, "login", date);
		bookingService.book(parkingUuid, "login", date);
		entityManager.flush();

		// then exception should be thrown - the user is already booked
	}

	@Test
	public void shouldCancelBooking() {
		// given
		LocalDate date = new DateTime().toLocalDate();
		User user1 = testUtilities.persistUser("login1");
		User user2 = testUtilities.persistUser("login2");
		Parking parking = testUtilities.persistParking(user1, user2);
		testUtilities.persistBooking(parking, date, user1, user2);
		entityManager.flush();

		// when
		String parkingUuid = parking.getUuid();
		bookingService.cancel(parkingUuid, "login1", date);

		// then
		Booking booking = bookingDao.find(parkingUuid, date);
		assertEquals(parkingUuid, booking.getParking().getUuid());

		Set<BookingEntry> entries = booking.getEntries();
		assertEquals(1, entries.size());

		BookingEntry entry = entries.iterator().next();
		assertEquals("login2", entry.getUser().getLogin());

		Set<BookingEntry> acceptedEntries = booking.getAcceptedEntries();
		assertEquals(1, acceptedEntries.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotCancelNonExistingBooking() {
		// given
		LocalDate date = new DateTime().toLocalDate();
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
		LocalDate date = new DateTime().toLocalDate();
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
		LocalDate date = new DateTime().toLocalDate();
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
		LocalDate date = new DateTime().toLocalDate();
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

	@Test(expected = Exception.class)
	public void shouldNotFindBookings() {
		// given
		Parking parking = testUtilities.persistParking();
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		LocalDate startDate = new DateTime().toLocalDate();
		LocalDate endDate = startDate.plusWeeks(1);
		bookingService.findBookings(parking.getName(), "login", startDate, endDate);

		// then exception should be thrown - user is not assigned to given parking
	}

	@Test
	public void shouldNotFindAllBookings() {
		// given
		testUtilities.persistUser("login");
		entityManager.flush();

		// when
		LocalDate startDate = new DateTime().toLocalDate();
		LocalDate endDate = startDate.plusWeeks(1);
		List<ParkingBookingDto> bookings = bookingService.findAllBookings("login", startDate, endDate);

		// then
		assertNotNull(bookings);
		assertTrue(bookings.isEmpty());
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

		LocalDate startDate = new DateTime().toLocalDate();
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
}
