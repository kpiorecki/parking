package com.kpiorecki.parking.ejb.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.ResourceProducer;

@RunWith(Arquillian.class)
public class ScheduleAlgorithmTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(ScheduleAlgorithm.class, ResourceProducer.class);
	}

	@Inject
	private ScheduleAlgorithm algorithm;

	@Inject
	private Logger logger;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Test
	public void shouldCreateSchedule1() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", true, 10));
		records.add(createRecord("u2", false, 12));
		records.add(createRecord("u3", false, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));

		Booking booking = createTestCase(records, entries, 20);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(booking, schedule, "u1");
	}

	@Test
	public void shouldCreateSchedule2() {
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
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(booking, schedule, "u1", "u2", "u3");
	}

	@Test
	public void shouldCreateSchedule3() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", false, 10));
		records.add(createRecord("u2", false, 10));
		records.add(createRecord("u3", true, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));
		entries.add(createEntry("u2", 9, 4));
		entries.add(createEntry("u3", 10, 4));

		Booking booking = createTestCase(records, entries, 20);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(booking, schedule, "u3", "u2", "u1");
	}

	@Test
	public void shouldCreateSchedule4() {
		// given
		List<Record> records = new ArrayList<>();
		records.add(createRecord("u1", false, 10));
		records.add(createRecord("u2", false, 10));
		records.add(createRecord("u3", true, 14));

		List<BookingEntry> entries = new ArrayList<>();
		entries.add(createEntry("u1", 10, 4));
		entries.add(createEntry("u2", 9, 4));
		entries.add(createEntry("u3", 10, 4));

		Booking booking = createTestCase(records, entries, 2);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(booking, schedule, "u3", "u2");
	}

	@Test
	public void shouldCreateSchedule5() {
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
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(booking, schedule, "u2", "u1");
	}

	private void validate(Booking booking, List<Record> schedule, String... logins) {
		assertEquals(logins.length, schedule.size());
		for (int i = 0; i < schedule.size(); ++i) {
			assertEquals(logins[i], schedule.get(i).getUser().getLogin());
		}
		logger.info(createLoggerMessage(booking, schedule));
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

	private BookingEntry createEntry(String login, int day, int month) {
		BookingEntry entry = new BookingEntry();
		entry.setUser(createUser(login));
		entry.setCreationTime(new DateTime(2015, month, day, 14, 0));

		return entry;
	}

	private User createUser(String login) {
		User user = new User();
		user.setLogin(login);

		return user;
	}

	private String createLoggerMessage(Booking booking, List<Record> schedule) {
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
			sb.append("\n").append(i++).append(". user=").append(entry.getUser().getLogin()).append(", date=")
					.append(dateFormatter.print(entry.getCreationTime()));
		}
		sb.append("\nschedule:");
		i = 1;
		for (Record record : schedule) {
			sb.append(createRecordMessage(record, i++));
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
