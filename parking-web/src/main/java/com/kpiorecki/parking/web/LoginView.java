package com.kpiorecki.parking.web;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;

import org.slf4j.Logger;

@ManagedBean
public class LoginView {

	@Inject
	private Logger logger;

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void loginAction() {
		logger.info("login user={}", username);
	}
}
