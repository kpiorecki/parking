package com.kpiorecki.parking.web.user.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Stateless;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;

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

	public BookingModel createModel(ParkingBookingDto parkingBooking) {
		if (parkingBooking.getBookingList().isEmpty()) {
			throw new IllegalArgumentException("booking list cannot be empty");
		}

		List<WeekModel> weekModels = createWeekModels(parkingBooking);
		List<DayModel> dayModels = createDayModels(parkingBooking);

		BookingModel bookingModel = new BookingModel();
		bookingModel.setParking(parkingBooking.getParking());
		bookingModel.setDayModels(dayModels);
		bookingModel.setWeekModels(weekModels);

		return bookingModel;
	}

	private List<DayModel> createDayModels(ParkingBookingDto parkingBooking) {
		List<BookingDto> bookingList = parkingBooking.getBookingList();
		List<DayModel> dayModels = new ArrayList<DayModel>(bookingList.size());

		int capacity = parkingBooking.getParking().getCapacity();
		for (BookingDto bookingDto : bookingList) {
			DayModel dayModel = createDayModel(bookingDto, capacity);
			dayModels.add(dayModel);
		}

		return dayModels;
	}

	private DayModel createDayModel(BookingDto booking, int parkingCapacity) {
		Set<BookingEntryDto> entries = booking.getEntries();

		List<String> acceptedUsers = new ArrayList<String>();
		List<String> rejectedUsers = new ArrayList<String>();

		Status status = Status.EMPTY;
		boolean selected = false;
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
				selected = true;
			}
		}
		sortUsersList(acceptedUsers);
		sortUsersList(rejectedUsers);

		DayModel dayModel = new DayModel();
		dayModel.setDate(booking.getDate());
		dayModel.setLocked(booking.getStatus() == BookingStatus.LOCKED);
		dayModel.setHoliday(booking.isHoliday());
		dayModel.setStatus(status);
		dayModel.setSelected(selected);
		dayModel.setAcceptedUsers(acceptedUsers);
		dayModel.setRejectedUsers(rejectedUsers);

		int availableCapacity = parkingCapacity - entries.size();
		dayModel.setAvailableCapacity(availableCapacity);

		return dayModel;
	}

	private List<WeekModel> createWeekModels(ParkingBookingDto parkingBooking) {
		List<WeekModel> weekModels = new ArrayList<WeekModel>();

		List<BookingDto> bookingList = parkingBooking.getBookingList();
		LocalDate startDate = bookingList.get(0).getDate();

		int weekColumnSpan = 0;
		int lastWeekNumber = startDate.getWeekOfWeekyear();
		for (BookingDto bookingDto : bookingList) {
			LocalDate date = bookingDto.getDate();

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
