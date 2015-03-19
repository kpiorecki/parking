package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

public class ParkingDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;
	private String name;
	private Integer capacity;
	private AddressDto address;
	private Integer version;

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

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParkingDto [uuid=");
		builder.append(uuid);
		builder.append(", name=");
		builder.append(name);
		builder.append(", capacity=");
		builder.append(capacity);
		builder.append(", address=");
		builder.append(address);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

}
