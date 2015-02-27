package com.kpiorecki.parking.core.dto;

import java.io.Serializable;

public abstract class UuidDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
