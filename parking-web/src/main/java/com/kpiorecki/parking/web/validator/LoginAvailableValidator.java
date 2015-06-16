package com.kpiorecki.parking.web.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.service.user.UserService;

@FacesValidator("loginAvailableValidator")
public class LoginAvailableValidator implements Validator {

	@Inject
	private Logger logger;

	@Inject
	private UserService userService;

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String login = value.toString();
		logger.info("checking if login {} is available", login);

		boolean loginAvailable = userService.isLoginAvailable(login);
		if (!loginAvailable) {
			logger.info("login {} is not available", login);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login: value is not available.", null);
			throw new ValidatorException(message);
		}
	}

}
