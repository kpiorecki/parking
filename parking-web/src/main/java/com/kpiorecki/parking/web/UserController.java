package com.kpiorecki.parking.web;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;

@ManagedBean
@SessionScoped
public class UserController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	private String login;
	private String firstName;
	private String lastName;
	private String email;

	public boolean isLoggedIn() {
		return login != null;
	}

	public String getLogin() {
		return login;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setLoggedInUser(UserDto user) {
		logger.info("setting logged in user {}", user);

		login = user.getLogin();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		email = user.getEmail();
	}

}
