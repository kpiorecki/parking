package com.kpiorecki.parking.web.view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.web.util.URLEncoder;

@ManagedBean
@RequestScoped
public class ActivationController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private UserService userService;

	@Inject
	private URLEncoder urlEncoder;

	@ManagedProperty(value = "#{messageController}")
	private MessageController messageController;

	@ManagedProperty(value = "#{userController}")
	private UserController userController;

	@ManagedProperty(value = "#{authController}")
	private AuthController authController;

	private String login;

	private String encodedActivationUuid;

	public void setMessageController(MessageController messageController) {
		this.messageController = messageController;
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	public void setAuthController(AuthController authController) {
		this.authController = authController;
	}

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
