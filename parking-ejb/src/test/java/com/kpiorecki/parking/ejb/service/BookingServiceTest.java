package com.kpiorecki.parking.ejb.service;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.kpiorecki.parking.ejb.IntegrationTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;

public class BookingServiceTest extends IntegrationTest {

	@Inject
	private BookingService bookingService;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	private EntityManager entityManager;

	private DateTime date;
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

		date = new DateTime();
		parking1 = testUtilities.persistParking(p1User1, p1User2);
		parking2 = testUtilities.persistParking(p2User1, p2User2);
		testUtilities.persistBooking(parking1, date, p1User1, p1User2);
	}

	@Test
	public void shouldBookParking() {
		// when
		bookingService.book(parking2.getUuid(), p2Login1, date);

		// then
		List<Booking> bookings = findBooking(parking2, date);
		assertEquals(1, bookings.size());
		assertEquals(parking2.getId(), bookings.get(0).getParking().getId());
	}

	@Test(expected = Exception.class)
	public void shouldNotBookParking() {
		// when
		bookingService.book(parking2.getUuid(), p1Login1, date);

		// then exception should be thrown - user is not assigned to parking
	}

	@Test
	public void shouldCancelBooking() {
		// when
		bookingService.cancel(parking1.getUuid(), p1Login1, date);

		// then
		List<Booking> bookings = findBooking(parking1, date);
		assertEquals(1, bookings.size());

		Booking booking = bookings.get(0);
		assertEquals(parking1.getId(), booking.getParking().getId());

		Set<BookingEntry> entries = booking.getEntries();
		assertEquals(1, entries.size());

		BookingEntry entry = entries.iterator().next();
		assertEquals(p1Login2, entry.getUser().getLogin());
	}

	@Test(expected = Exception.class)
	public void shouldNotCancelBooking() {
		// when
		bookingService.cancel(parking2.getUuid(), p1Login1, date);

		// then exception should be thrown - booking does not exist
	}

	private List<Booking> findBooking(Parking parking, DateTime date) {
		TypedQuery<Booking> findQuery = entityManager.createNamedQuery("Booking.findByParkingAndDate", Booking.class);
		findQuery.setParameter("parkingId", parking.getId());
		findQuery.setParameter("date", date);
		return findQuery.getResultList();
	}
}
