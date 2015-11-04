package com.kpiorecki.parking.web.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.view.ViewScoped;
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
@ViewScoped
public class BookingOverviewController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String START_DATE_SESSION_KEY = BookingOverviewController.class.getName() + "START_DATE";

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
	private transient DateTimeFormatter dateFormatter;

	private List<BookingModel> bookingModels;

	public List<BookingModel> getBookingModels() {
		return bookingModels;
	}

	public void loadAllUserBookings() {
		LocalDate startDate = getStartDate();
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

	public LocalDate getStartDate() {
		Map<String, Object> sessionMap = externalContext.getSessionMap();
		LocalDate startDate = (LocalDate) sessionMap.get(START_DATE_SESSION_KEY);
		if (startDate == null) {
			startDate = getDefaultDate();
		}
		return startDate;
	}

	public LocalDate getDefaultDate() {
		return new LocalDate().withDayOfMonth(1);
	}

	public String gotoPreviousMonth() {
		addToStartDate(-1);
		return "pretty:";
	}

	public String gotoCurrentMonth() {
		externalContext.getSessionMap().remove(START_DATE_SESSION_KEY);
		return "pretty:";
	}

	public String gotoNextMonth() {
		addToStartDate(1);
		return "pretty:";
	}

	private void addToStartDate(int months) {
		LocalDate newStartDate = getStartDate().plusMonths(months);
		externalContext.getSessionMap().put(START_DATE_SESSION_KEY, newStartDate);
	}

}
