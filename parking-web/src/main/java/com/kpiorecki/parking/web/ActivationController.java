package com.kpiorecki.parking.web;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.web.util.WebUtil;

@Named
@RequestScoped
public class ActivationController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private transient Logger logger;

	@Inject
	private transient UserService userService;

	@Inject
	private transient WebUtil webUtil;

	@Inject
	private transient ExternalContext externalContext;
	
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

	public String activate() throws IOException {
		logger.info("activating user with parameter={}", encodedActivationUuid);

		String activationUuid = webUtil.decode(encodedActivationUuid);
		if (activationUuid != null) {
			UserDto user = userService.activateUser(activationUuid);
			if (user != null) {
				logger.info("invalidating current session");
				externalContext.invalidateSession();

				login = user.getLogin();
				authController.setLogin(login);

				return null;
			}
		}

		return webUtil.navigateToMessage("activation-wrong-param");
	}
}
