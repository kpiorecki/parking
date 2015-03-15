package com.kpiorecki.parking.ejb;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.kpiorecki.parking.ejb.entity.Address;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingEntry;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@Stateless
public class TestUtilities {

	@Inject
	private EntityManager entityManager;

	@Inject
	private UuidGenerator uuidGenerator;

	public User persistUser(String login) {
		User user = new User();
		user.setLogin(login);
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setEmail(login + "@mail.com");

		entityManager.persist(user);
		entityManager.flush();

		return user;
	}

	public Parking persistParking(User... users) {
		Address address = new Address();
		address.setCity("city");
		address.setPostalCode("code");
		address.setStreet("street");
		address.setNumber("number");

		Set<Record> records = new HashSet<>();
		for (User user : users) {
			Record record = new Record();
			record.setUser(user);
			record.setVip(false);
			record.setPoints(0);

			records.add(record);
		}

		Parking parking = new Parking();
		parking.setUuid(uuidGenerator.generateUuid());
		parking.setAddress(address);
		parking.setCapacity(50);
		parking.setName("name");
		parking.setRecords(records);

		entityManager.persist(parking);
		entityManager.flush();

		return parking;
	}

	public Parking persistParkingWithUsers(String... logins) {
		User[] users = new User[logins.length];
		for (int i = 0; i < logins.length; ++i) {
			users[i] = persistUser(logins[i]);
		}
		return persistParking(users);
	}

	public Booking persistBooking(Parking parking, DateTime date, User... users) {
		Set<BookingEntry> entries = new HashSet<>();
		for (User user : users) {
			BookingEntry entry = new BookingEntry();
			entry.setUser(user);
			entries.add(entry);
		}

		Booking booking = new Booking();
		booking.setParking(parking);
		booking.setDate(date);
		booking.setEntries(entries);

		entityManager.persist(booking);
		entityManager.flush();

		return booking;
	}
}
