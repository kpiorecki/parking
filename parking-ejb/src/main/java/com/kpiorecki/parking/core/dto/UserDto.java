package com.kpiorecki.parking.core.dto;

import java.io.Serializable;

public class UserDto extends UuidDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String firstName;
	private String lastName;
	private Integer version;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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
		builder.append("UserDto [firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", version=");
		builder.append(version);
		builder.append(", uuid=");
		builder.append(getUuid());
		builder.append("]");

		return builder.toString();
	}

}
