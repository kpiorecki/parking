package com.kpiorecki.parking.ejb.service.parking.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;

public class ParkingProcessorTest {

	private ParkingProcessor processor = new ParkingProcessor();

	@Test
	public void shouldCalculateNewRecordPoints() {
		shouldCalculateNewRecordPointsImpl(0);
		shouldCalculateNewRecordPointsImpl(1, 1);
		shouldCalculateNewRecordPointsImpl(2, 1, 2);
		shouldCalculateNewRecordPointsImpl(2, 1, 2, 2, 2);
		shouldCalculateNewRecordPointsImpl(2, 1, 3);
		shouldCalculateNewRecordPointsImpl(3, 1, 4);
		shouldCalculateNewRecordPointsImpl(13, 10, 12, 14, 16);
		shouldCalculateNewRecordPointsImpl(501, 1000, 1);
	}

	private void shouldCalculateNewRecordPointsImpl(int expectedNewRecordPoints, int... recordsPoints) {
		// given
		Parking parking = new Parking();
		for (int recordPoints : recordsPoints) {
			Record record = new Record();
			record.setPoints(recordPoints);
			parking.addRecord(record);
		}

		// when
		int newRecordPoints = processor.calculateNewRecordPoints(parking);

		// then
		assertEquals(expectedNewRecordPoints, newRecordPoints);
	}
}
