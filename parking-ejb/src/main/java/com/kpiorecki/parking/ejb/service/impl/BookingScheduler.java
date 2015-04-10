package com.kpiorecki.parking.ejb.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.google.common.collect.Ordering;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
public class BookingScheduler {

	@Inject
	private Logger logger;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	public void updateSchedule(Booking booking) {
		logger.info("updating schedule for parking={} and date={}", booking.getParking().getUuid(),
				dateFormatter.print(booking.getDate()));

		assignSchedule(booking);
	}

	public void lockSchedule(Booking booking) {
		Parking parking = booking.getParking();
		logger.info("locking schedule for parking={} and date={}", parking.getUuid(),
				dateFormatter.print(booking.getDate()));

		List<Element> schedule = assignSchedule(booking);
		addRecordPoints(parking, schedule);
	}

	private List<Element> assignSchedule(Booking booking) {
		Parking parking = booking.getParking();
		List<Element> elements = merge(booking.getEntries(), parking.getRecords());
		Ordering<Element> ordering = Ordering.from(new ElementComparator());
		List<Element> schedule = ordering.leastOf(elements, parking.getCapacity());

		Set<BookingEntry> acceptedEntries = new HashSet<>(schedule.size());
		for (Element element : schedule) {
			acceptedEntries.add(element.getBookingEntry());
		}
		booking.acceptEntries(acceptedEntries);

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
			 * first earlier, then later creation times
			 */
			return e1.getCreationTime().compareTo(e2.getCreationTime());
		}
	}

}
