package com.kpiorecki.parking.web.user.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.kpiorecki.parking.ejb.dto.UserDto;

public class UserModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserModel() {
	}

	public UserModel(UserDto user) {
		login = user.getLogin();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		email = user.getEmail();
	}

	@Size(min = UserDto.LOGIN_MIN_LEN, max = UserDto.LOGIN_MAX_LEN)
	private String login;

	@Size(max = UserDto.FIRSTNAME_MAX_LEN)
	private String firstName;

	@Size(max = UserDto.LASTNAME_MAX_LEN)
	private String lastName;

	@Email
	@Size(max = UserDto.EMAIL_MAX_LEN)
	private String email;

	@Size(min = UserDto.PASSWORD_MIN_LEN, max = UserDto.PASSWORD_MAX_LEN)
	private String password;
	
	private String oldPassword;

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

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getLabelLogin() {
		return "Login";
	}

	public String getLabelFirstName() {
		return "First Name";
	}

	public String getLabelLastName() {
		return "Last Name";
	}

	public String getLabelEmail() {
		return "Email";
	}

	public String getLabelPassword() {
		return "Password";
	}

	public String getLabelRepeatPassword() {
		return "Repeat Password";
	}

	public String getLabelOldPassword() {
		return "Old Password";
	}

	public String getLabelNewPassword() {
		return "New Password";
	}
}
