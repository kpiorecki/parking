package com.kpiorecki.parking.web.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.service.booking.BookingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.web.user.model.BookingModel;
import com.kpiorecki.parking.web.user.model.BookingModelFactory;
import com.kpiorecki.parking.web.user.model.DayModel;

@Named
@ViewScoped
public class BookingController implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * maximum year supported by joda-time
	 */
	private static final int MAX_YEAR = 292278993;

	/**
	 * minimum year allowed
	 */
	private static final int MIN_YEAR = 1970;

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

	private int year;

	private int month;

	private String parkingName;

	private BookingModel bookingModel;

	private Set<DayModel> dirtyModels = new HashSet<>();

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getParkingName() {
		return parkingName;
	}

	public void setParkingName(String parkingName) {
		this.parkingName = parkingName;
	}

	public BookingModel getBookingModel() {
		return bookingModel;
	}

	public void validateYear(FacesContext context, UIComponent component, Object value) {
		validateValue((Integer) value, MIN_YEAR, MAX_YEAR);
	}

	public void validateMonth(FacesContext context, UIComponent component, Object value) {
		validateValue((Integer) value, DateTimeConstants.JANUARY, DateTimeConstants.DECEMBER);
	}

	public void loadUserBookings() throws IOException {
		LocalDate startDate = new LocalDate().withYear(year).withMonthOfYear(month).withDayOfMonth(1);
		LocalDate endDate = startDate.plusMonths(1);
		String login = externalContext.getRemoteUser();

		logger.info("loading bookings for parkingName={}, user={}, startDate={}, endDate={}", parkingName, login,
				dateFormatter.print(startDate), dateFormatter.print(endDate));
		try {
			ParkingBookingDto parkingBooking = bookingService.findBookings(parkingName, login, startDate, endDate);
			bookingModel = bookingModelFactory.createModel(parkingBooking);
		} catch (RuntimeException e) {
			logger.info(String.format("did not find bookings for parkingName=%s", parkingName), e);
			externalContext.responseSendError(HttpServletResponse.SC_NOT_FOUND, "could not find parking booking");
		}
	}

	public void save() {
		// TODO
	}

	public void updateDirty(DayModel model) {
		String date = dateFormatter.print(model.getDate());
		if (dirtyModels.contains(model)) {
			logger.info("removing {} model from dirty set", date);
			dirtyModels.remove(model);
		} else {
			logger.info("adding {} model to dirty set", date);
			dirtyModels.add(model);
		}
	}

	public boolean isDirty() {
		return !dirtyModels.isEmpty();
	}

	private void validateValue(Integer value, int min, int max) {
		if (value == null || value < min || value > max) {
			throw new ValidatorException(new FacesMessage(String.format("invalid value %d", value)));
		}
	}

}
