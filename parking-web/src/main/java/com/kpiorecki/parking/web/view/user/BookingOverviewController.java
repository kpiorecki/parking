package com.kpiorecki.parking.web.view.user;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.service.booking.BookingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.web.view.UserController;

@Named
@RequestScoped
public class BookingOverviewController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private BookingService bookingService;

	@Inject
	private UserController userController;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	private List<ParkingBookingDto> userBookings;

	public List<ParkingBookingDto> getUserBookings() {
		return userBookings;
	}

	public void loadUserBookings() {
		LocalDate startDate = new LocalDate().withDayOfMonth(1);
		LocalDate endDate = startDate.plusMonths(1);
		String login = userController.getLogin();

		logger.info("loading bookings for user={}, startDate={}, endDate={}", login, dateFormatter.print(startDate),
				dateFormatter.print(endDate));
		userBookings = bookingService.findUserBookings(login, startDate, endDate);
	}
}
