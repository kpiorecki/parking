package com.kpiorecki.parking.ejb.service.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.google.common.collect.Ordering;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.exception.DomainException;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
public class ScheduleAlgorithm {

	@Inject
	private Logger logger;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	public List<Record> createSchedule(Booking booking) {
		Parking parking = booking.getParking();
		logger.info("creating schedule for parking={} and date={}", parking.getUuid(),
				dateFormatter.print(booking.getDate()));

		Map<Record, DateTime> records = mapEntriesToRecords(booking);
		RecordComparator recordComparator = new RecordComparator(records);

		Ordering<Record> recordOrdering = Ordering.from(recordComparator);
		List<Record> schedule = recordOrdering.leastOf(records.keySet(), parking.getCapacity());

		return schedule;
	}

	private Map<Record, DateTime> mapEntriesToRecords(Booking booking) {
		Set<Record> records = booking.getParking().getRecords();
		Map<String, Record> loginRecords = new HashMap<>();
		for (Record record : records) {
			loginRecords.put(record.getUser().getLogin(), record);
		}

		Set<BookingEntry> entries = booking.getEntries();
		Map<Record, DateTime> recordTimestamps = new HashMap<>(entries.size());
		for (BookingEntry entry : entries) {
			String login = entry.getUser().getLogin();
			Record record = loginRecords.get(login);
			if (record == null) {
				String message = String.format("did not find login=%s record in parking=%s", login, booking
						.getParking().getUuid());
				logger.error(message);
				throw new DomainException(message);
			}
			recordTimestamps.put(record, entry.getCreationTime());
		}

		return recordTimestamps;
	}

	private static class RecordComparator implements Comparator<Record> {

		private Map<Record, DateTime> recordBookingTimestamps;

		public RecordComparator(Map<Record, DateTime> recordBookingTimestamps) {
			this.recordBookingTimestamps = recordBookingTimestamps;
		}

		@Override
		public int compare(Record r1, Record r2) {
			int result = compareVip(r1, r2);
			if (result == 0) {
				result = comparePoints(r1, r2);
				if (result == 0) {
					result = compareCreationTimes(r1, r2);
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

		private int compareCreationTimes(Record r1, Record r2) {
			/**
			 * first earlier, then later creation times
			 */
			DateTime r1Timestamp = recordBookingTimestamps.get(r1);
			DateTime r2Timestamp = recordBookingTimestamps.get(r2);

			return r1Timestamp.compareTo(r2Timestamp);
		}
	}

}
