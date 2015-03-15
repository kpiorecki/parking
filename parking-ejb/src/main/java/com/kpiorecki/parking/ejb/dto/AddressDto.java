package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

public class AddressDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String street;
	private String number;
	private String postalCode;
	private String city;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AddressDto [street=");
		builder.append(street);
		builder.append(", number=");
		builder.append(number);
		builder.append(", postalCode=");
		builder.append(postalCode);
		builder.append(", city=");
		builder.append(city);
		builder.append("]");
		return builder.toString();
	}

}
