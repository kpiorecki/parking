package com.kpiorecki.parking.ejb.util;

public enum Image {

	BOOKING_ASSIGNED("booking-assigned.png", "image/png", 100, 100),
	BOOKING_REVOKED("booking-revoked.png", "image/png", 100, 100),
	REGISTER_CONFIRM("register-confirm.png", "image/png", 100, 100),
	PARKING_LOGO("parking-logo.png", "image/png", 80, 61);

	private final String fileName;
	private final String mimeType;
	private final int width;
	private final int height;

	private Image(String fileName, String mimeType, int width, int height) {
		this.fileName = fileName;
		this.mimeType = mimeType;
		this.width = width;
		this.height = height;
	}

	public String getFilePath() {
		return "/images/" + fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
