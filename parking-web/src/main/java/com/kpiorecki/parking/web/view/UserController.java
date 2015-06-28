package com.kpiorecki.parking.web.view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;

@ManagedBean
@SessionScoped
public class UserController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private ExternalContext externalContext;

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

	public String logout() {
		if (isLoggedIn()) {
			logger.info("logging out user {}", login);

			login = null;
			firstName = null;
			lastName = null;
			email = null;
		}
		logger.info("invalidating http session");
		externalContext.invalidateSession();

		return "pretty:index";
	}
}
