package com.kpiorecki.parking.core.service;

import org.joda.time.DateTime;

public interface BookingService {

	boolean book(String parkingUuid, String userUuid, DateTime date);

	boolean cancel(String parkingUuid, String userUuid, DateTime date);
}
