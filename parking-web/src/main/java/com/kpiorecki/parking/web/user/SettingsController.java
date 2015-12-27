package com.kpiorecki.parking.web.user;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.service.user.impl.UserPasswordEncoder;
import com.kpiorecki.parking.web.MessageController;
import com.kpiorecki.parking.web.user.model.UserModel;

@Named
@ViewScoped
public class SettingsController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private ExternalContext externalContext;

	@Inject
	private UserService userService;

	@Inject
	private UserPasswordEncoder passwordEncoder;

	@Inject
	private MessageController messageController;

	private UserModel userModel;

	private UserDto user;

	public void loadUser() {
		String login = externalContext.getRemoteUser();

		logger.info("loading user={}", login);
		user = userService.findUser(login);
		userModel = new UserModel(user);
	}

	public String saveAccount() {
		user.setFirstName(userModel.getFirstName());
		user.setLastName(userModel.getLastName());
		user.setEmail(userModel.getEmail());

		return save("account-save");
	}

	public String savePassword() {
		user.setPassword(passwordEncoder.encode(userModel.getPassword()));
		return save("password-save");
	}

	public void validateOldPassword(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		logger.info("checking if old password matches user input");
		if (value != null) {
			String oldPassword = passwordEncoder.encode(value.toString());
			if (!oldPassword.equals(user.getPassword())) {
				logger.info("old password does not match user input");
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
						String.format("%s: invalid value.", userModel.getLabelOldPassword()), null);
				throw new ValidatorException(message);
			}
		}
	}

	public UserModel getUserModel() {
		return userModel;
	}

	private String save(String messageId) {
		logger.info("saving user={}", user);
		userService.modifyUser(user);

		messageController.setMessageId(messageId);
		return "/WEB-INF/view/message.xhtml";
	}

}
