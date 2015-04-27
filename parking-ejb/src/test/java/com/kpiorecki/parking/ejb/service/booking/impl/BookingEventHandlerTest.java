package com.kpiorecki.parking.ejb.service.booking.impl;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import com.icegreen.greenmail.util.GreenMailUtil;
import com.kpiorecki.parking.ejb.GreenMailTest;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.entity.Booking;
import com.kpiorecki.parking.ejb.entity.Booking.Status;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.ejb.util.ResourceProducer;

@RunWith(Arquillian.class)
public class BookingEventHandlerTest extends GreenMailTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
				.addClasses(BookingEventHandler.class, TestUtilities.class)
				.addPackage(ResourceProducer.class.getPackage());
	}

	@Inject
	private BookingEventHandler eventHandler;

	@Inject
	private TestUtilities testUtilities;

	@Inject
	private Logger logger;

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
	public void shouldSendAssignedEmail() throws Exception {
		// given
		BookingEvent event = createEvent();

		// when
		eventHandler.onAssignedEvent(event);

		// then
		validateOneEmailSent();
	}

	@Test
	public void shouldSendRevokedEmail() throws Exception {
		// given
		BookingEvent event = createEvent();

		// when
		eventHandler.onRevokedEvent(event);

		// then
		validateOneEmailSent();
	}

	private BookingEvent createEvent() {
		BookingEvent event = new BookingEvent();
		event.setDate(date);
		event.setParking(parking);
		event.setUser(user);
		event.setBookingStatus(booking.getStatus());

		return event;
	}

	private void validateOneEmailSent() throws Exception {
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertTrue(receivedMessages.length == 1);

		MimeMessage message = receivedMessages[0];
		logger.info("sent one email from={}, to={}, subject={}, body:\n{}", message.getFrom(),
				message.getRecipients(RecipientType.TO), message.getSubject(), GreenMailUtil.getBody(message));
	}
}
