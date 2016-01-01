package com.kpiorecki.parking.web;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.service.user.impl.UserPasswordEncoder;
import com.kpiorecki.parking.ejb.util.UuidGenerator;
import com.kpiorecki.parking.web.user.model.UserModel;
import com.kpiorecki.parking.web.util.WebUtil;

@Named
@ViewScoped
public class ResetPasswordController implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String MESSAGE_ID_SUCCEEDED = "password-save";
	private static final String MESSAGE_ID_FAILED = "reset-password-wrong-param";

	@Inject
	private Logger logger;

	@Inject
	private FacesContext context;

	@Inject
	private WebUtil webUtil;

	@Inject
	private UserService userService;

	@Inject
	private UuidGenerator uuidGenerator;

	@Inject
	private UserPasswordEncoder passwordEncoder;

	private UserModel userModel = new UserModel();

	private String encodedResetPasswordUuid;

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getEncodedResetPasswordUuid() {
		return encodedResetPasswordUuid;
	}

	public void setEncodedResetPasswordUuid(String encodedResetPasswordUuid) {
		this.encodedResetPasswordUuid = encodedResetPasswordUuid;
	}

	public String sendResetRequest() {
		String login = userModel.getLogin();
		logger.info("sending reset password request to user={}", login);

		String resetPasswordUuid = uuidGenerator.generateUuid();

		String encodedUuid = webUtil.encode(resetPasswordUuid);
		String resetPasswordURL = webUtil.getContextRootURL() + "/reset-password/" + encodedUuid;

		boolean succeeded = userService.requestResetPassword(login, resetPasswordUuid, resetPasswordURL);
		if (succeeded) {
			return webUtil.navigateToMessage("reset-password-request");
		} else {
			logger.info("sending reset password request to user={} failed", login);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login: user could not be found.",
					null);
			context.addMessage("forgot-password-messages", message);
			return null;
		}
	}

	public String processResetRequest() {
		logger.info("processing reset password request with parameter={}", encodedResetPasswordUuid);

		String resetPasswordUuid = webUtil.decode(encodedResetPasswordUuid);
		if (resetPasswordUuid != null) {
			String login = userService.loadResetPasswordLogin(resetPasswordUuid);
			if (login != null) {
				userModel.setLogin(login);
				return null;
			}
		}

		return webUtil.navigateToMessage(MESSAGE_ID_FAILED);
	}

	public String resetPassword() {
		String login = userModel.getLogin();
		logger.info("resetting user={} password", login);

		String encodedPassword = passwordEncoder.encode(userModel.getPassword());
		boolean succeeded = userService.resetPassword(login, encodedPassword);
		return webUtil.navigateToMessage(succeeded ? MESSAGE_ID_SUCCEEDED : MESSAGE_ID_FAILED);
	}

}
