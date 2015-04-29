package com.kpiorecki.parking.ejb.util;

public enum Image {

	BOOKING_ASSIGNED("booking-assigned.png", "image/png"),
	BOOKING_REVOKED("booking-revoked.png", "image/png"),
	PARKING_HEADER("parking-header.png", "image/png");

	private final String fileName;
	private final String mimeType;

	private Image(String fileName, String mimeType) {
		this.fileName = fileName;
		this.mimeType = mimeType;
	}

	public String getFilePath() {
		return "/images/" + fileName;
	}

	public String getMimeType() {
		return mimeType;
	}
}
