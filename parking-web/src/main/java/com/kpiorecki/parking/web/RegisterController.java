package com.kpiorecki.parking.web;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.service.user.impl.UserPasswordEncoder;
import com.kpiorecki.parking.ejb.util.UuidGenerator;
import com.kpiorecki.parking.web.user.model.UserModel;
import com.kpiorecki.parking.web.util.URLEncoder;

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
	private URLEncoder urlEncoder;

	@Inject
	private UuidGenerator uuidGenerator;

	@Inject
	private ExternalContext externalContext;

	@Inject
	private MessageController messageController;

	private UserModel userModel = new UserModel();

	public String register() {
		logger.info("registering new user");

		UserDto user = createUser();
		String activationUuid = uuidGenerator.generateUuid();

		String encodedUuid = urlEncoder.encode(activationUuid);
		String activationURL = getContextRootURL() + "/activation/" + encodedUuid;

		userService.registerUser(user, activationUuid, activationURL);

		messageController.setMessageId("register-info");
		return "/WEB-INF/view/message.xhtml";
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

	private String getContextRootURL() {
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String requestURL = request.getRequestURL().toString();
		String rootURL = requestURL.substring(0, requestURL.length() - request.getRequestURI().length());
		return rootURL + request.getContextPath();
	}

}
