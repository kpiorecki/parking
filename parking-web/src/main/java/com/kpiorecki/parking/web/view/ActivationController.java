package com.kpiorecki.parking.web.view;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.web.util.URLEncoder;

@Named
@RequestScoped
public class ActivationController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private UserService userService;

	@Inject
	private URLEncoder urlEncoder;

	@Inject
	private MessageController messageController;

	@Inject
	private UserController userController;

	@Inject
	private AuthController authController;

	private String login;

	private String encodedActivationUuid;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEncodedActivationUuid() {
		return encodedActivationUuid;
	}

	public void setEncodedActivationUuid(String encodedActivationUuid) {
		this.encodedActivationUuid = encodedActivationUuid;
	}

	public String activate() {
		logger.info("activating user with parameter={}", encodedActivationUuid);

		String activationUuid = urlEncoder.decode(encodedActivationUuid);
		if (activationUuid != null) {
			UserDto user = userService.activateUser(activationUuid);
			if (user != null) {
				userController.logout();
				login = user.getLogin();
				authController.setLogin(login);

				return null;
			}
		}

		messageController.setMessageId("activation-wrong-param");
		return "/WEB-INF/view/message.xhtml";
	}
}
