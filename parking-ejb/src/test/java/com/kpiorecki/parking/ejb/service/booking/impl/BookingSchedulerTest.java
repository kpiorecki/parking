package com.kpiorecki.parking.ejb.service.booking.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.ResourceProducer;

@RunWith(Arquillian.class)
public class BookingSchedulerTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBaseDeployment().addClasses(BookingScheduler.class, ResourceProducer.class);
	}

	@Inject
	private BookingScheduler scheduler;

	@Inject
	private Logger logger;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Test
	public void shouldLockSchedule1() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", true, 10));
		records.add(createRecord("u2", false, 12));
		records.add(createRecord("u3", false, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));

		Booking booking = createTestCase(records, entries, 20);

		// when
		scheduler.lockSchedule(booking);

		// then
		validate(booking, "u1");
		validatePoints(booking, "u1", 11);
	}

	@Test
	public void shouldLockSchedule2() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", false, 10));
		records.add(createRecord("u2", false, 12));
		records.add(createRecord("u3", false, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));
		entries.add(createEntry("u2", 10, 4));
		entries.add(createEntry("u3", 10, 4));

		Booking booking = createTestCase(records, entries, 20);

		// when
		scheduler.lockSchedule(booking);

		// then
		validate(booking, "u1", "u2", "u3");
		validatePoints(booking, "u1", 11);
		validatePoints(booking, "u2", 13);
		validatePoints(booking, "u3", 15);
	}

	@Test
	public void shouldLockSchedule3() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", false, 10));
		records.add(createRecord("u2", false, 10));
		records.add(createRecord("u3", true, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1"));
		entries.add(createEntry("u2", 9, 4));
		entries.add(createEntry("u3", 10, 4));

		Booking booking = createTestCase(records, entries, 2);

		// when
		scheduler.lockSchedule(booking);

		// then
		validate(booking, "u3", "u2");
		validatePoints(booking, "u3", 15);
		validatePoints(booking, "u2", 11);
	}

	@Test
	public void shouldLockSchedule4() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", false, 10));
		records.add(createRecord("u2", true, 14));

		// situation where users u3, u4 and u5 were removed after making parking bookings
		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));
		entries.add(createEntry("u2", 9, 4));
		entries.add(createEntry("u3", 10, 4));
		entries.add(createEntry("u4", 8, 4));
		entries.add(createEntry("u5", 7, 4));

		Booking booking = createTestCase(records, entries, 10);

		// when
		scheduler.lockSchedule(booking);

		// then
		validate(booking, "u2", "u1");
		validatePoints(booking, "u2", 15);
		validatePoints(booking, "u1", 11);
	}

	@Test
	public void shouldLockHolidaySchedule() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", false, 10));
		records.add(createRecord("u2", false, 12));
		records.add(createRecord("u3", false, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));
		entries.add(createEntry("u2", 10, 4));
		entries.add(createEntry("u3", 10, 4));

		Booking booking = createTestCase(records, entries, 20);

		// holiday schedule that marks every day as holiday
		HolidaySchedule holidaySchedule = new HolidaySchedule();
		for (int day = DateTimeConstants.MONDAY; day <= DateTimeConstants.SUNDAY; ++day) {
			holidaySchedule.addDayOfWeek(day);
		}
		booking.getParking().setHolidaySchedule(holidaySchedule);

		// when
		scheduler.lockSchedule(booking);

		// then
		validate(booking);
		validatePoints(booking, "u1", 10);
		validatePoints(booking, "u2", 12);
		validatePoints(booking, "u3", 14);
	}

	private void validatePoints(Booking booking, String login, Integer expectedPoints) {
		Set<Record> records = booking.getParking().getRecords();
		for (Record record : records) {
			if (record.getUser().getLogin().equals(login)) {
				assertEquals(expectedPoints, record.getPoints());
				return;
			}
		}
		fail(String.format("did not find user=%s record", login));
	}

	private void validate(Booking booking, String... logins) {
		Set<BookingEntry> acceptedEntries = booking.getAcceptedEntries();
		assertEquals(logins.length, acceptedEntries.size());

		Set<String> acceptedLogins = new HashSet<>(acceptedEntries.size());
		for (BookingEntry entry : acceptedEntries) {
			acceptedLogins.add(entry.getUser().getLogin());
		}
		for (int i = 0; i < logins.length; ++i) {
			assertTrue(acceptedLogins.contains(logins[i]));
		}

		logger.info(createLoggerMessage(booking));
	}

	private Booking createTestCase(List<Record> records, List<BookingEntry> entries, int capacity) {
		Parking parking = new Parking();
		parking.setUuid("uuid");
		parking.setCapacity(capacity);
		for (Record record : records) {
			parking.addRecord(record);
		}

		Booking booking = new Booking();
		booking.setDate(new LocalDate());
		booking.setParking(parking);
		for (BookingEntry entry : entries) {
			booking.addEntry(entry);
		}

		return booking;
	}

	private Record createRecord(String login, boolean vip, int points) {
		Record record = new Record();
		record.setUser(createUser(login));
		record.setVip(vip);
		record.setPoints(points);

		return record;
	}

	private BookingEntry createEntry(String login) {
		BookingEntry entry = new BookingEntry();
		entry.setUser(createUser(login));

		return entry;
	}

	private BookingEntry createEntry(String login, int day, int month) {
		BookingEntry entry = createEntry(login);
		entry.setCreationTime(new DateTime(2015, month, day, 14, 0));

		return entry;
	}

	private User createUser(String login) {
		User user = new User();
		user.setLogin(login);

		return user;
	}

	private String createLoggerMessage(Booking booking) {
		StringBuilder sb = new StringBuilder();
		Parking parking = booking.getParking();

		sb.append("booking for ").append(parking.getCapacity()).append(" places\nrecords:");
		int i = 1;
		for (Record record : parking.getRecords()) {
			sb.append(createRecordMessage(record, i++));
		}
		sb.append("\nbooking entries:");
		i = 1;
		for (BookingEntry entry : booking.getEntries()) {
			sb.append("\n").append(i++).append(". user=").append(entry.getUser().getLogin()).append(", date=");
			DateTime creationTime = entry.getCreationTime();
			if (creationTime != null) {
				sb.append(dateFormatter.print(creationTime));
			} else {
				sb.append("empty");
			}
			if (entry.getAccepted()) {
				sb.append(" - accepted");
			}
		}

		return sb.toString();
	}

	private String createRecordMessage(Record record, int number) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n").append(number).append(". user=").append(record.getUser().getLogin()).append(", vip=")
				.append(record.getVip()).append(", points=").append(record.getPoints());
		return sb.toString();
	}
}
