package com.kpiorecki.parking.web;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.Address;
import com.kpiorecki.parking.ejb.entity.Booking.Status;
import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.service.booking.impl.BookingEvent;
import com.kpiorecki.parking.ejb.service.booking.impl.BookingEventHandler;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@WebServlet(value = "/test")
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private UserService userService;

	@Inject
	private BookingEventHandler bookingEventHandler;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		addUser(resp);
		listUsers(resp);
		fireOnAssignedEvent(resp);
	}

	private void addUser(HttpServletResponse resp) throws IOException {
		long millis = new DateTime().getMillis();

		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("login_" + millis);
		user.setEmail("user_" + millis + "@mail.com");
		user.setPassword("password");

		userService.addUser(user);

		String message = "added user " + user;
		logger.info(message);

		resp.getWriter().println(message);
	}

	private void listUsers(HttpServletResponse resp) throws IOException {
		List<UserDto> allUsers = userService.findAllUsers();
		StringBuilder sb = new StringBuilder();
		sb.append("\n\nuser list:");
		int i = 1;
		for (UserDto userDto : allUsers) {
			sb.append("\n").append(i++).append(". ").append(userDto);
		}

		String message = sb.toString();
		logger.info(message);

		resp.getWriter().println(message);
	}

	private void fireOnAssignedEvent(HttpServletResponse resp) throws IOException {
		String message = "\n\nfiring onAssignedEvent event  should send email";
		logger.info(message);
		resp.getWriter().println(message);

		Address address = new Address();
		address.setCity("city");
		address.setPostalCode("code");
		address.setStreet("street");
		address.setNumber("number");

		Parking parking = new Parking();
		parking.setUuid("uuid");
		parking.setAddress(address);
		parking.setCapacity(50);
		parking.setName("name");

		User user = new User();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("login");
		user.setEmail("user@mail.com");

		Record record = new Record();
		record.setUser(user);
		record.setVip(false);
		record.setPoints(0);

		parking.addRecord(record);

		BookingEvent event = new BookingEvent();
		event.setDate(new LocalDate());
		event.setParking(parking);
		event.setUser(user);
		event.setBookingStatus(Status.RELEASED);

		bookingEventHandler.onAssignedEvent(event);
	}
}