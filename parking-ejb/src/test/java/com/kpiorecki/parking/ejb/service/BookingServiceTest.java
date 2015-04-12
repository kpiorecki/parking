package com.kpiorecki.parking.ejb.service;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.kpiorecki.parking.ejb.IntegrationTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.dao.BookingDao;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.Booking.Status;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;

public class BookingServiceTest extends IntegrationTest {

	@Inject
	private BookingService bookingService;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	private EntityManager entityManager;

	@Inject
	private BookingDao bookingDao;

	private LocalDate date;
	private Parking parking1;
	private Parking parking2;

	private String p1Login1 = "p1Login1";
	private String p1Login2 = "p1Login2";

	private String p2Login1 = "p2Login1";
	private String p2Login2 = "p2Login2";

	@Before
	public void prepareData() {
		User p1User1 = testUtilities.persistUser(p1Login1);
		User p1User2 = testUtilities.persistUser(p1Login2);

		User p2User1 = testUtilities.persistUser(p2Login1);
		User p2User2 = testUtilities.persistUser(p2Login2);

		date = new DateTime().toLocalDate();
		parking1 = testUtilities.persistParking(p1User1, p1User2);
		parking2 = testUtilities.persistParking(p2User1, p2User2);
		testUtilities.persistBooking(parking1, date, p1User1, p1User2);

		entityManager.flush();
	}

	@Test
	public void shouldBookParking() {
		// when
		String parking2Uuid = parking2.getUuid();
		bookingService.book(parking2Uuid, p2Login1, date);

		// then
		Booking booking = bookingDao.find(parking2Uuid, date);
		assertEquals(parking2Uuid, booking.getParking().getUuid());

		Set<BookingEntry> acceptedEntries = booking.getAcceptedEntries();
		assertEquals(1, acceptedEntries.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotBookParkingForUnassignedUser() {
		// when
		bookingService.book(parking2.getUuid(), p1Login1, date);

		// then exception should be thrown - user is not assigned to parking
	}

	@Test(expected = Exception.class)
	public void shouldNotBookParkingTwice() {
		// when
		String parking2Uuid = parking2.getUuid();
		bookingService.book(parking2Uuid, p2Login1, date);
		bookingService.book(parking2Uuid, p2Login1, date);

		entityManager.flush();

		// then exception should be thrown - the user is already booked
	}

	@Test
	public void shouldCancelBooking() {
		// when
		String parking1Uuid = parking1.getUuid();
		bookingService.cancel(parking1Uuid, p1Login1, date);

		// then
		Booking booking = bookingDao.find(parking1Uuid, date);
		assertEquals(parking1Uuid, booking.getParking().getUuid());

		Set<BookingEntry> entries = booking.getEntries();
		assertEquals(1, entries.size());

		BookingEntry entry = entries.iterator().next();
		assertEquals(p1Login2, entry.getUser().getLogin());

		Set<BookingEntry> acceptedEntries = booking.getAcceptedEntries();
		assertEquals(1, acceptedEntries.size());
	}

	@Test(expected = Exception.class)
	public void shouldNotCancelNonExistingBooking() {
		// when
		bookingService.cancel(parking2.getUuid(), p1Login1, date);

		// then exception should be thrown - booking does not exist
	}

	@Test
	public void shouldReleaseBooking() {
		// when
		String parking1Uuid = parking1.getUuid();
		bookingService.release(parking1Uuid, date);

		// then
		Booking booking = bookingDao.find(parking1Uuid, date);
		assertEquals(Status.RELEASED, booking.getStatus());
	}

	@Test(expected = Exception.class)
	public void shouldNotReleaseBookingTwice() {
		// when
		String parking1Uuid = parking1.getUuid();
		bookingService.release(parking1Uuid, date);
		bookingService.release(parking1Uuid, date);

		// then exception should be thrown - booking already released
	}

	@Test
	public void shouldLockBooking() {
		// when
		String parking1Uuid = parking1.getUuid();
		bookingService.lock(parking1Uuid, date);

		// then
		Booking booking = bookingDao.find(parking1Uuid, date);
		assertEquals(Status.LOCKED, booking.getStatus());

		Set<Record> records = parking1.getRecords();
		for (Record record : records) {
			assertEquals(Integer.valueOf(1), record.getPoints());
		}
	}

}
