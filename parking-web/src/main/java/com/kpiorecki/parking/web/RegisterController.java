package com.kpiorecki.parking.web;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.service.user.impl.UserPasswordEncoder;
import com.kpiorecki.parking.ejb.util.UuidGenerator;
import com.kpiorecki.parking.web.user.model.UserModel;
import com.kpiorecki.parking.web.util.WebUtil;

@Named
@RequestScoped
public class RegisterController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private UserService userService;

	@Inject
	private UserPasswordEncoder passwordEncoder;

	@Inject
	private UuidGenerator uuidGenerator;

	@Inject
	private WebUtil webUtil;

	private UserModel userModel = new UserModel();

	public String register() {
		logger.info("registering new user");

		UserDto user = createUser();
		String activationUuid = uuidGenerator.generateUuid();

		String encodedUuid = webUtil.encode(activationUuid);
		String activationURL = webUtil.getContextRootURL() + "/activation/" + encodedUuid;

		userService.registerUser(user, activationUuid, activationURL);

		return webUtil.navigateToMessage("register-info");
	}

	public UserModel getUserModel() {
		return userModel;
	}

	private UserDto createUser() {
		UserDto user = new UserDto();
		user.setLogin(userModel.getLogin());
		user.setFirstName(userModel.getFirstName());
		user.setLastName(userModel.getLastName());
		user.setEmail(userModel.getEmail());
		user.setPassword(passwordEncoder.encode(userModel.getPassword()));

		return user;
	}

}
