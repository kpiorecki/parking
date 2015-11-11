package com.kpiorecki.parking.ejb.service.booking.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule;
import com.kpiorecki.parking.ejb.entity.HolidaySchedule.DateStatus;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
public class BookingScheduler {

	@Inject
	private Logger logger;

	@Inject
	@BookingAssigned
	private Event<BookingEvent> assignedEvent;

	@Inject
	@BookingRevoked
	private Event<BookingEvent> revokedEvent;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	public void updateSchedule(Booking booking, Set<BookingEntry> previousAcceptedEntries) {
		logger.info("updating schedule for parking={} and date={}", booking.getParking().getUuid(),
				dateFormatter.print(booking.getDate()));

		generateSchedule(booking, previousAcceptedEntries);
	}

	public void lockSchedule(Booking booking) {
		Parking parking = booking.getParking();
		logger.info("locking schedule for parking={} and date={}", parking.getUuid(),
				dateFormatter.print(booking.getDate()));

		List<Element> schedule = generateSchedule(booking, booking.getAcceptedEntries());
		addRecordPoints(parking, schedule);
	}

	private List<Element> generateSchedule(Booking booking, Set<BookingEntry> previousAcceptedEntries) {
		Parking parking = booking.getParking();
		Set<BookingEntry> nextAcceptedEntries = new HashSet<>();
		List<Element> schedule = new ArrayList<>();

		if (isBookingAllowed(parking, booking)) {
			List<Element> elements = merge(booking.getEntries(), parking.getRecords());
			Ordering<Element> ordering = Ordering.from(new ElementComparator());
			schedule = ordering.leastOf(elements, parking.getCapacity());
			for (Element element : schedule) {
				nextAcceptedEntries.add(element.getBookingEntry());
			}
		}

		booking.acceptEntries(nextAcceptedEntries);
		fireEvents(booking, previousAcceptedEntries, nextAcceptedEntries);

		return schedule;
	}

	private void addRecordPoints(Parking parking, List<Element> schedule) {
		for (Element element : schedule) {
			element.getRecord().addPoint();
		}
	}

	private List<Element> merge(Set<BookingEntry> entries, Set<Record> records) {
		Map<String, Record> loginRecords = new HashMap<>();
		for (Record record : records) {
			loginRecords.put(record.getUser().getLogin(), record);
		}
		List<Element> elements = new ArrayList<>();
		for (BookingEntry entry : entries) {
			String login = entry.getUser().getLogin();
			Record record = loginRecords.get(login);
			/**
			 * user might be removed after parking booking - in such case his login will not be found in loginRecords
			 * map, so will not be included by scheduling algorithm
			 */
			if (record != null) {
				Element element = new Element(entry, record);
				elements.add(element);
			}
		}

		return elements;
	}

	private void fireEvents(Booking booking, Set<BookingEntry> previousAcceptedEntries, Set<BookingEntry> nextAcceptedEntries) {
		SetView<BookingEntry> revokedEntries = Sets.difference(previousAcceptedEntries, nextAcceptedEntries);
		for (BookingEntry revokedEntry : revokedEntries) {
			BookingEvent event = createEvent(booking, revokedEntry);
			revokedEvent.fire(event);
		}

		SetView<BookingEntry> assignedEntries = Sets.difference(nextAcceptedEntries, previousAcceptedEntries);
		for (BookingEntry assignedEntry : assignedEntries) {
			BookingEvent event = createEvent(booking, assignedEntry);
			assignedEvent.fire(event);
		}
	}

	private BookingEvent createEvent(Booking booking, BookingEntry entry) {
		BookingEvent event = new BookingEvent();
		event.setDate(booking.getDate());
		event.setParking(booking.getParking());
		event.setUser(entry.getUser());
		event.setBookingStatus(booking.getStatus());

		return event;
	}

	private boolean isBookingAllowed(Parking parking, Booking booking) {
		HolidaySchedule holidaySchedule = parking.getHolidaySchedule();
		if (holidaySchedule != null) {
			DateStatus dateStatus = holidaySchedule.getDateStatus(booking.getDate());
			return !dateStatus.isHoliday();
		}
		return true;
	}

	private static class Element {

		private BookingEntry bookingEntry;
		private Record record;

		public Element(BookingEntry bookingEntry, Record record) {
			this.bookingEntry = bookingEntry;
			this.record = record;
		}

		public BookingEntry getBookingEntry() {
			return bookingEntry;
		}

		public Record getRecord() {
			return record;
		}
	}

	private static class ElementComparator implements Comparator<Element> {

		@Override
		public int compare(Element e1, Element e2) {
			Record r1 = e1.getRecord();
			Record r2 = e2.getRecord();

			int result = compareVip(r1, r2);
			if (result == 0) {
				result = comparePoints(r1, r2);
				if (result == 0) {
					result = compareCreationTimes(e1.getBookingEntry(), e2.getBookingEntry());
				}
			}

			return result;
		}

		private int compareVip(Record r1, Record r2) {
			/**
			 * first true, then false - opposite to natural order
			 */
			return Boolean.compare(r2.getVip(), r1.getVip());
		}

		private int comparePoints(Record r1, Record r2) {
			/**
			 * first smaller, then bigger - natural order
			 */
			return Integer.compare(r1.getPoints(), r2.getPoints());
		}

		private int compareCreationTimes(BookingEntry e1, BookingEntry e2) {
			/**
			 * first earlier, then later creation times (<code>null</code> creation times mean that BookingEntry has not
			 * been saved yet and are treated as <code>now()</code> values by DateTimeComparator).
			 */
			DateTime t1 = e1.getCreationTime();
			DateTime t2 = e2.getCreationTime();

			return DateTimeComparator.getInstance().compare(t1, t2);
		}
	}

}
