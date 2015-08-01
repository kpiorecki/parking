package com.kpiorecki.parking.web.user.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;

import com.kpiorecki.parking.ejb.dto.BookingDto;
import com.kpiorecki.parking.ejb.dto.ParkingBookingDto;
import com.kpiorecki.parking.ejb.dto.ParkingDto;

public class BookingModelFactoryTest {

	private BookingModelFactory modelFactory = new BookingModelFactory();

	@Test
	public void shouldCreateModelForMonth() {
		// given
		ParkingBookingDto parkingBooking = createParkingBooking();
		LocalDate startDate = new LocalDate(2015, 7, 1);
		LocalDate endDate = new LocalDate(2015, 8, 1);

		// when
		BookingModel model = modelFactory.createModel(parkingBooking, startDate, endDate);

		// then
		validateModel(model, startDate, 31, new int[] { 27, 28, 29, 30, 31 });
	}

	@Test
	public void shouldCreateModelForOneDay() {
		// given
		ParkingBookingDto parkingBooking = createParkingBooking();
		LocalDate startDate = new LocalDate(2015, 7, 3);
		LocalDate endDate = new LocalDate(2015, 7, 4);

		// when
		BookingModel model = modelFactory.createModel(parkingBooking, startDate, endDate);

		// then
		validateModel(model, startDate, 1, new int[] { 27 });
	}

	@Test
	public void shouldCreateModelForOneWeek() {
		// given
		ParkingBookingDto parkingBooking = createParkingBooking();
		LocalDate startDate = new LocalDate(2015, 8, 3);
		LocalDate endDate = new LocalDate(2015, 8, 10);

		// when
		BookingModel model = modelFactory.createModel(parkingBooking, startDate, endDate);

		// then
		validateModel(model, startDate, 7, new int[] { 32 });
	}

	@Test
	public void shouldCreateModelForCustomDays() {
		// given
		ParkingBookingDto parkingBooking = createParkingBooking();
		LocalDate startDate = new LocalDate(2015, 8, 9);
		LocalDate endDate = new LocalDate(2015, 9, 1);

		// when
		BookingModel model = modelFactory.createModel(parkingBooking, startDate, endDate);

		// then
		validateModel(model, startDate, 23, new int[] { 32, 33, 34, 35, 36 });
	}

	@Test
	public void shouldCreateModelForPreviousYearWeekNumber() {
		// given
		ParkingBookingDto parkingBooking = createParkingBooking();
		LocalDate startDate = new LocalDate(2016, 1, 1);
		LocalDate endDate = new LocalDate(2016, 1, 14);

		// when
		BookingModel model = modelFactory.createModel(parkingBooking, startDate, endDate);

		// then
		validateModel(model, startDate, 13, new int[] { 53, 1, 2 });
	}

	@Test
	public void shouldCreateModelForYearChange() {
		// given
		ParkingBookingDto parkingBooking = createParkingBooking();
		LocalDate startDate = new LocalDate(2015, 12, 25);
		LocalDate endDate = new LocalDate(2016, 2, 25);

		// when
		BookingModel model = modelFactory.createModel(parkingBooking, startDate, endDate);

		// then
		validateModel(model, startDate, 62, new int[] { 52, 53, 1, 2, 3, 4, 5, 6, 7, 8 });
	}

	private ParkingBookingDto createParkingBooking() {
		ParkingBookingDto parkingBooking = new ParkingBookingDto();
		parkingBooking.setParking(createParking());
		parkingBooking.setBookingList(new ArrayList<BookingDto>());

		return parkingBooking;
	}

	private ParkingDto createParking() {
		ParkingDto parkingDto = new ParkingDto();
		parkingDto.setName("name");
		parkingDto.setCapacity(20);
		parkingDto.setUuid("uuid");

		return parkingDto;
	}

	private void validateModel(BookingModel model, LocalDate startDate, int numberOfDays, int[] weekNumbers) {
		assertNotNull(model);
		validateDayModels(model, startDate, numberOfDays);
		validateWeekModels(model, weekNumbers, numberOfDays);
	}

	private void validateDayModels(BookingModel model, LocalDate startDate, int numberOfDays) {
		List<DayModel> dayModels = model.getDayModels();
		assertNotNull(dayModels);

		assertEquals(numberOfDays, dayModels.size());
		for (int i = 0; i < numberOfDays; ++i) {
			LocalDate expectedDate = startDate.plusDays(i);
			assertEquals(expectedDate, dayModels.get(i).getDate());
		}
	}

	private void validateWeekModels(BookingModel model, int[] weekNumbers, int numberOfDays) {
		List<WeekModel> weekModels = model.getWeekModels();
		assertNotNull(weekModels);

		assertEquals(weekNumbers.length, weekModels.size());
		int totalColumnSpan = 0;
		for (int i = 0; i < weekNumbers.length; ++i) {
			WeekModel weekModel = weekModels.get(i);
			assertTrue(weekModel.getColumnSpan() > 0);
			assertEquals(weekNumbers[i], weekModel.getWeek());

			totalColumnSpan += weekModel.getColumnSpan();
		}
		assertEquals(numberOfDays, totalColumnSpan);
	}
}
