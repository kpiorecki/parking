package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.GreenMailTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.Booking.Status;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.util.ApplicationSetup;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.MailSender;
import com.kpiorecki.parking.ejb.util.ResourceProducer;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@RunWith(Arquillian.class)
public class BookingEventHandlerTest extends GreenMailTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBaseDeployment().addClasses(BookingEventHandler.class, TestUtilities.class,
				ResourceProducer.class, UuidGenerator.class, MailSender.class, ApplicationSetup.class);
	}

	@Inject
	private BookingEventHandler eventHandler;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	private LocalDate date;
	private User user;
	private Parking parking;
	private Booking booking;

	@Before
	public void prepareData() {
		date = new LocalDate(2015, 4, 10);
		user = testUtilities.createUser("login");
		parking = testUtilities.createParking(user);
		booking = testUtilities.createBooking(parking, date, user);
		booking.setStatus(Status.RELEASED);
	}

	@Test
	public void shouldSendAssignedEmail() {
		// given
		BookingEvent event = createEvent();

		// when
		eventHandler.onAssignedEvent(event);

		// then
		assertOneMailSent();
	}

	@Test
	public void shouldSendRevokedEmail() {
		// given
		BookingEvent event = createEvent();

		// when
		eventHandler.onRevokedEvent(event);

		// then
		assertOneMailSent();
	}

	private BookingEvent createEvent() {
		BookingEvent event = new BookingEvent();
		event.setDate(date);
		event.setParking(parking);
		event.setUser(user);
		event.setBookingStatus(booking.getStatus());

		return event;
	}

}
