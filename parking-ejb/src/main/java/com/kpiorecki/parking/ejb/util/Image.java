package com.kpiorecki.parking.ejb.util;

public enum Image {

	BOOKING_ASSIGNED("booking-assigned.png"),
	BOOKING_REVOKED("booking-revoked.png"),
	PARKING_HEADER("parking-header.png");

	private final String fileName;

	private Image(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return "/images/" + fileName;
	}
}
