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
import com.kpiorecki.parking.web.user.model.UserModel;
import com.kpiorecki.parking.web.util.WebUtil;

@Named
@ViewScoped
public class SettingsController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private transient Logger logger;

	@Inject
	private transient ExternalContext externalContext;

	@Inject
	private transient UserService userService;

	@Inject
	private transient UserPasswordEncoder passwordEncoder;

	@Inject
	private transient WebUtil webUtil;

	private UserModel userModel = new UserModel();

	private UserDto user = new UserDto();

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

		return webUtil.navigateToMessage(messageId);
	}

}
