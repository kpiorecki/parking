package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.GreenMailIT;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.util.ApplicationSetup;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.MailSender;
import com.kpiorecki.parking.ejb.util.ResourceProducer;
import com.kpiorecki.parking.ejb.util.SyncMailSender;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@RunWith(Arquillian.class)
public class BookingEventHandlerIT extends GreenMailIT {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBaseDeployment().addClasses(BookingEventHandler.class, TestUtilities.class,
				ResourceProducer.class, UuidGenerator.class, MailSender.class, SyncMailSender.class,
				ApplicationSetup.class);
	}

	@Inject
	private BookingEventHandler eventHandler;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Test
	public void shouldSendAssignedEmail() {
		// given
		BookingEvent event = createEvent(BookingStatus.RELEASED);

		// when
		eventHandler.onAssignedEvent(event);

		// then
		assertOneMailSent();
	}

	@Test
	public void shouldSendRevokedEmail() {
		// given
		BookingEvent event = createEvent(BookingStatus.RELEASED);

		// when
		eventHandler.onRevokedEvent(event);

		// then
		assertOneMailSent();
	}

	@Test
	public void shouldNotSendEmailsInDraftStatus() {
		// given
		BookingEvent event = createEvent(BookingStatus.DRAFT);

		// when
		eventHandler.onAssignedEvent(event);
		eventHandler.onRevokedEvent(event);

		// then
		assertNoMailsSent();
	}

	private BookingEvent createEvent(BookingStatus status) {
		LocalDate date = new LocalDate(2015, 4, 10);
		User user = testUtilities.createUser("login");
		Parking parking = testUtilities.createParking(user);
		Booking booking = testUtilities.createBooking(parking, date, user);
		booking.updateStatus(status);

		BookingEvent event = new BookingEvent();
		event.setDate(date);
		event.setParking(parking);
		event.setUser(user);
		event.setBookingStatus(booking.getStatus());

		return event;
	}

}
