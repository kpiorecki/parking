package com.kpiorecki.parking.web.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.service.booking.BookingService;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.web.user.model.BookingModel;
import com.kpiorecki.parking.web.user.model.BookingModelFactory;
import com.kpiorecki.parking.web.user.model.DayModel;

@Named
@ViewScoped
public class BookingController implements Serializable {

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
	private transient DateTimeFormatter dateFormatter;

	private int year;

	private int month;

	private String encodedParkingUuid;

	private BookingModel bookingModel;

	private Set<DayModel> dirtyModels = new HashSet<>();

	public void setYear(int year) {
		this.year = year;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public void setEncodedParkingUuid(String encodedParkingUuid) {
		this.encodedParkingUuid = encodedParkingUuid;
	}

	public BookingModel getBookingModel() {
		return bookingModel;
	}

	public void loadBooking() {
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

}
