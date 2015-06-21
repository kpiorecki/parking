package com.kpiorecki.parking.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.kpiorecki.parking.ejb.dto.AddressDto;

@Embeddable
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(length = AddressDto.STREET_MAX_LEN)
	private String street;

	@Column(length = AddressDto.NUMBER_MAX_LEN)
	private String number;

	@Column(length = AddressDto.POSTAL_CODE_MAX_LEN)
	private String postalCode;

	@Column(length = AddressDto.CITY_MAX_LEN)
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

}
