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

	/**
	 * UserModel is used for password validation
	 */
	private UserModel userModel = new UserModel();

	private String login;

	private String encodedResetPasswordUuid;

	private String resetPasswordUuid;

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEncodedResetPasswordUuid() {
		return encodedResetPasswordUuid;
	}

	public void setEncodedResetPasswordUuid(String encodedResetPasswordUuid) {
		this.encodedResetPasswordUuid = encodedResetPasswordUuid;
	}

	public String sendResetRequest() {
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

		resetPasswordUuid = webUtil.decode(encodedResetPasswordUuid);
		if (resetPasswordUuid != null) {
			boolean valid = userService.isResetPasswordValid(resetPasswordUuid);
			if (valid) {
				return null;
			}
		}

		return webUtil.navigateToMessage(MESSAGE_ID_FAILED);
	}

	public String resetPassword() {
		logger.info("resetting password for resetPasswordUuid={}", resetPasswordUuid);

		String encodedPassword = passwordEncoder.encode(userModel.getPassword());
		boolean succeeded = userService.resetPassword(resetPasswordUuid, encodedPassword);
		return webUtil.navigateToMessage(succeeded ? MESSAGE_ID_SUCCEEDED : MESSAGE_ID_FAILED);
	}

}
