package com.kpiorecki.parking.ejb.dto;

import java.io.Serializable;

public class UserDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int LOGIN_MIN_LEN = 4;
	public static final int LOGIN_MAX_LEN = 64;
	public static final int FIRSTNAME_MAX_LEN = 255;
	public static final int LASTNAME_MAX_LEN = 255;
	public static final int EMAIL_MAX_LEN = 255;
	public static final int PASSWORD_MIN_LEN = 8;
	public static final int PASSWORD_MAX_LEN = 255;

	private String login;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Integer version;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
		builder.append("UserDto [login=");
		builder.append(login);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(email);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");

		return builder.toString();
	}
}
