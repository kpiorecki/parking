package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

public class HolidayScheduleBaseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int NAME_MAX_LEN = 255;

	private String uuid;
	private String name;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HolidayScheduleBaseDto [uuid=");
		builder.append(uuid);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");

		return builder.toString();
	}

}
