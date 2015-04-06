package com.kpiorecki.parking.ejb.service.impl;

import static org.junit.Assert.assertEquals;

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
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
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

	@Test
	public void shouldFindProperRecords1() {
		// given
		Record[] records = new Record[] { createRecord("u1", true, 10), createRecord("u2", false, 12),
				createRecord("u3", false, 14) };
		BookingEntry[] entries = new BookingEntry[] { createEntry("u1", 10, 4) };
		Booking booking = createTestCase(records, entries, 20);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(schedule, "u1");
	}

	@Test
	public void shouldFindProperRecords2() {
		// given
		Record[] records = new Record[] { createRecord("u1", false, 10), createRecord("u2", false, 12),
				createRecord("u3", false, 14) };
		BookingEntry[] entries = new BookingEntry[] { createEntry("u1", 10, 4), createEntry("u2", 10, 4),
				createEntry("u3", 10, 4) };
		Booking booking = createTestCase(records, entries, 20);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(schedule, "u1", "u2", "u3");
	}

	@Test
	public void shouldFindProperRecords3() {
		// given
		Record[] records = new Record[] { createRecord("u1", false, 10), createRecord("u2", false, 10),
				createRecord("u3", true, 14) };
		BookingEntry[] entries = new BookingEntry[] { createEntry("u1", 10, 4), createEntry("u2", 9, 4),
				createEntry("u3", 10, 4) };
		Booking booking = createTestCase(records, entries, 20);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(schedule, "u3", "u2", "u1");
	}

	@Test
	public void shouldFindProperRecords4() {
		// given
		Record[] records = new Record[] { createRecord("u1", false, 10), createRecord("u2", false, 10),
				createRecord("u3", true, 14) };
		BookingEntry[] entries = new BookingEntry[] { createEntry("u1", 10, 4), createEntry("u2", 9, 4),
				createEntry("u3", 10, 4) };
		Booking booking = createTestCase(records, entries, 2);

		// when
		List<Record> schedule = algorithm.createSchedule(booking);

		// then
		validate(schedule, "u3", "u2");
	}

	private void validate(List<Record> schedule, String... logins) {
		assertEquals(logins.length, schedule.size());
		for (int i = 0; i < schedule.size(); ++i) {
			assertEquals(logins[i], schedule.get(i).getUser().getLogin());
		}
	}

	private Booking createTestCase(Record[] records, BookingEntry[] entries, int capacity) {
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
}
