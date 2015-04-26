package com.kpiorecki.parking.ejb;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.joda.time.LocalDate;

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
		User user = createUser(login);
		entityManager.persist(user);

		return user;
	}

	public Parking persistParking(User... users) {
		Parking parking = createParking(users);
		entityManager.persist(parking);

		return parking;
	}

	public Booking persistBooking(Parking parking, LocalDate date, User... users) {
		Booking booking = createBooking(parking, date, users);
		entityManager.persist(booking);

		return booking;
	}

	public User createUser(String login) {
		User user = new User();
		user.setLogin(login);
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setEmail(login + "@mail.com");
		return user;
	}

	public Parking createParking(User... users) {
		Address address = new Address();
		address.setCity("city");
		address.setPostalCode("code");
		address.setStreet("street");
		address.setNumber("number");

		Parking parking = new Parking();
		parking.setUuid(uuidGenerator.generateUuid());
		parking.setAddress(address);
		parking.setCapacity(50);
		parking.setName("name");

		for (User user : users) {
			Record record = new Record();
			record.setUser(user);
			record.setVip(false);
			record.setPoints(0);

			parking.addRecord(record);
		}
		return parking;
	}

	public Booking createBooking(Parking parking, LocalDate date, User... users) {
		Booking booking = new Booking();
		booking.setParking(parking);
		booking.setDate(date);
		for (User user : users) {
			BookingEntry entry = new BookingEntry();
			entry.setUser(user);

			booking.addEntry(entry);
		}
		return booking;
	}
}
