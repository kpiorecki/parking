package com.kpiorecki.parking.web.user.model;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.util.DateFormatter;

@Stateless
public class BookingModelFactory {

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	public BookingModel createModel(ParkingBookingDto parkingBooking, LocalDate startDate, LocalDate endDate) {
		if (!endDate.isAfter(startDate)) {
			throw new IllegalArgumentException(String.format("endDate=%s is not after startDate=%s",
					dateFormatter.print(endDate), dateFormatter.print(startDate)));
		}

		int daysNumber = Days.daysBetween(startDate, endDate).getDays();
		List<DayModel> dayModels = new ArrayList<DayModel>(daysNumber);
		List<WeekModel> weekModels = new ArrayList<WeekModel>();

		int weekColumnSpan = 0;
		int lastWeekNumber = startDate.getWeekOfWeekyear();
		for (int i = 0; i < daysNumber; ++i) {
			LocalDate date = startDate.plusDays(i);

			// create and add DayModel for current date
			DayModel dayModel = new DayModel();
			dayModel.setDate(date);
			dayModels.add(dayModel);

			// check in new WeekModel should be added
			int weekNumber = date.getWeekOfWeekyear();
			if (weekNumber != lastWeekNumber) {
				weekModels.add(createWeekModel(weekColumnSpan, lastWeekNumber));
				lastWeekNumber = weekNumber;
				weekColumnSpan = 1;
			} else {
				weekColumnSpan++;
			}
		}
		// add remaining WeekModel
		weekModels.add(createWeekModel(weekColumnSpan, lastWeekNumber));

		BookingModel bookingModel = new BookingModel();
		bookingModel.setParking(parkingBooking.getParking());
		bookingModel.setDayModels(dayModels);
		bookingModel.setWeekModels(weekModels);

		return bookingModel;
	}

	private WeekModel createWeekModel(int weekColumnSpan, int weekNumber) {
		WeekModel weekModel = new WeekModel();
		weekModel.setColumnSpan(weekColumnSpan);
		weekModel.setWeek(weekNumber);

		return weekModel;
	}
}
