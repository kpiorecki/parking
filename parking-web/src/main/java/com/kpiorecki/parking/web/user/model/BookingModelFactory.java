package com.kpiorecki.parking.web.user.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;

import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import com.kpiorecki.parking.ejb.dto.BookingDto;
import com.kpiorecki.parking.ejb.dto.BookingEntryDto;
import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.entity.BookingStatus;
import com.kpiorecki.parking.ejb.util.DateFormatter;
import com.kpiorecki.parking.web.user.model.DayModel.Status;

@Stateless
public class BookingModelFactory {

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Inject
	private ExternalContext externalContext;

	public BookingModel createModel(ParkingBookingDto parkingBooking, LocalDate startDate, LocalDate endDate) {
		if (!endDate.isAfter(startDate)) {
			throw new IllegalArgumentException(String.format("endDate=%s is not after startDate=%s",
					dateFormatter.print(endDate), dateFormatter.print(startDate)));
		}

		int daysNumber = Days.daysBetween(startDate, endDate).getDays();
		List<WeekModel> weekModels = createWeekModels(startDate, daysNumber);
		List<DayModel> dayModels = createDayModels(parkingBooking, startDate, daysNumber);

		BookingModel bookingModel = new BookingModel();
		bookingModel.setParking(parkingBooking.getParking());
		bookingModel.setDayModels(dayModels);
		bookingModel.setWeekModels(weekModels);

		return bookingModel;
	}

	private List<DayModel> createDayModels(ParkingBookingDto parkingBooking, LocalDate startDate, int daysNumber) {
		List<DayModel> dayModels = new ArrayList<DayModel>(daysNumber);
		Map<LocalDate, DayModel> daysMap = new HashMap<>(daysNumber);

		int capacity = parkingBooking.getParking().getCapacity();
		for (int i = 0; i < daysNumber; ++i) {
			LocalDate date = startDate.plusDays(i);

			// create and add DayModel for current date
			DayModel dayModel = new DayModel();
			dayModel.setDate(date);
			dayModel.setAvailableCapacity(capacity);
			dayModel.setEnabled(isDayEnabled(date));

			dayModels.add(dayModel);
			daysMap.put(date, dayModel);
		}

		// update DayModels content based on booking list
		for (BookingDto booking : parkingBooking.getBookingList()) {
			DayModel dayModel = daysMap.get(booking.getDate());
			if (dayModel == null) {
				throw new IllegalArgumentException(String.format("did not find DayModel for %s",
						dateFormatter.print(booking.getDate())));
			}
			updateDateModel(dayModel, booking, capacity);
		}

		return dayModels;
	}

	private boolean isDayEnabled(LocalDate date) {
		switch (date.getDayOfWeek()) {
		case DateTimeConstants.SATURDAY:
		case DateTimeConstants.SUNDAY:
			return false;
		default:
			return true;
		}
	}

	private void updateDateModel(DayModel dayModel, BookingDto booking, int parkingCapacity) {
		boolean editable = (booking.getStatus() != BookingStatus.LOCKED);
		dayModel.setEditable(editable);

		Set<BookingEntryDto> entries = booking.getEntries();
		int availableCapacity = parkingCapacity - entries.size();
		dayModel.setAvailableCapacity(availableCapacity);

		List<String> acceptedUsers = new ArrayList<String>();
		List<String> rejectedUsers = new ArrayList<String>();
		Status status = Status.EMPTY;
		String currentUser = externalContext.getRemoteUser();

		for (BookingEntryDto entry : entries) {
			Boolean accepted = entry.getAccepted();
			String login = entry.getLogin();
			if (accepted) {
				acceptedUsers.add(login);
			} else {
				rejectedUsers.add(login);
			}
			if (Objects.equals(currentUser, login)) {
				status = accepted ? Status.ACCEPTED : Status.REJECTED;
			}
		}
		sortUsersList(acceptedUsers);
		sortUsersList(rejectedUsers);

		dayModel.setStatus(status);
		dayModel.setAcceptedUsers(acceptedUsers);
		dayModel.setRejectedUsers(rejectedUsers);
	}

	private List<WeekModel> createWeekModels(LocalDate startDate, int daysNumber) {
		List<WeekModel> weekModels = new ArrayList<WeekModel>();

		int weekColumnSpan = 0;
		int lastWeekNumber = startDate.getWeekOfWeekyear();
		for (int i = 0; i < daysNumber; ++i) {
			LocalDate date = startDate.plusDays(i);

			// check if new WeekModel should be added
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

		return weekModels;
	}

	private WeekModel createWeekModel(int weekColumnSpan, int weekNumber) {
		WeekModel weekModel = new WeekModel();
		weekModel.setColumnSpan(weekColumnSpan);
		weekModel.setWeek(weekNumber);

		return weekModel;
	}

	private void sortUsersList(List<String> users) {
		Collections.sort(users, String.CASE_INSENSITIVE_ORDER);
	}
}
