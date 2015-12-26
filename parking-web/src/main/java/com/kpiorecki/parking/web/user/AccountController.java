package com.kpiorecki.parking.web.user;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.web.user.model.UserModel;

@Named
@ViewScoped
public class AccountController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private ExternalContext externalContext;

	@Inject
	private UserService userService;

	private UserModel userModel;

	private UserDto user;

	public void loadUser() {
		String login = externalContext.getRemoteUser();

		logger.info("loading user={}", login);
		user = userService.findUser(login);
		userModel = new UserModel(user);
	}

	public String saveUser() {
		user.setFirstName(userModel.getFirstName());
		user.setLastName(userModel.getLastName());
		user.setEmail(userModel.getEmail());

		logger.info("saving user={}", user);
		userService.modifyUser(user);

		return "pretty:user-home";
	}

	public UserModel getUserModel() {
		return userModel;
	}

}
