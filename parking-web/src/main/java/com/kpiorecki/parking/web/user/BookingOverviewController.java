package com.kpiorecki.parking.web.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.service.booking.BookingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.web.user.model.BookingModel;
import com.kpiorecki.parking.web.user.model.BookingModelFactory;

@Named
@RequestScoped
public class BookingOverviewController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private BookingService bookingService;

	@Inject
	private ExternalContext externalContext;

	@Inject
	private BookingModelFactory bookingModelFactory;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	private List<BookingModel> bookingModels;

	public List<BookingModel> getBookingModels() {
		return bookingModels;
	}

	public void loadAllUserBookings() {
		LocalDate startDate = new LocalDate().withDayOfMonth(1);
		LocalDate endDate = startDate.plusMonths(1);
		String login = externalContext.getRemoteUser();

		logger.info("loading bookings for user={}, startDate={}, endDate={}", login, dateFormatter.print(startDate),
				dateFormatter.print(endDate));
		List<ParkingBookingDto> parkingBookings = bookingService.findAllBookings(login, startDate, endDate);

		bookingModels = new ArrayList<BookingModel>(parkingBookings.size());
		for (ParkingBookingDto parkingBooking : parkingBookings) {
			BookingModel bookingModel = bookingModelFactory.createModel(parkingBooking);
			bookingModels.add(bookingModel);
		}
	}

}
