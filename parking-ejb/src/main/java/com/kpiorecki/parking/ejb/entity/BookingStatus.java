package com.kpiorecki.parking.ejb.entity;

import com.kpiorecki.parking.ejb.util.DomainException;

public enum BookingStatus {
	DRAFT,
	RELEASED,
	LOCKED;

	public static BookingStatus getHigherStatus(BookingStatus status1, BookingStatus status2) {
		if (status1.compareTo(status2) < 0) {
			return status2;
		} else {
			return status1;
		}
	}

	public static void validateLowerStatus(BookingStatus status, BookingStatus border) {
		if (status != null && status.compareTo(border) >= 0) {
			throw new DomainException(
					String.format("invalid booking status - %s is not lower than %s", status, border));
		}
	}

}