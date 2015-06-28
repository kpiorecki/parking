package com.kpiorecki.parking.web.view;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.service.user.impl.UserPasswordEncoder;
import com.kpiorecki.parking.ejb.util.UuidGenerator;
import com.kpiorecki.parking.web.util.URLEncoder;

@ManagedBean
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

	@ManagedProperty(value = "#{messageController}")
	private MessageController messageController;

	@Size(min = UserDto.LOGIN_MIN_LEN, max = UserDto.LOGIN_MAX_LEN)
	private String login;

	@Size(max = UserDto.FIRSTNAME_MAX_LEN)
	private String firstName;

	@Size(max = UserDto.LASTNAME_MAX_LEN)
	private String lastName;

	@Email
	@Size(max = UserDto.EMAIL_MAX_LEN)
	private String email;

	@Size(min = UserDto.PASSWORD_MIN_LEN, max = UserDto.PASSWORD_MAX_LEN)
	private String password;

	public void setMessageController(MessageController messageController) {
		this.messageController = messageController;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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

	private UserDto createUser() {
		UserDto user = new UserDto();
		user.setLogin(login);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));

		return user;
	}

	private String getContextRootURL() {
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		String requestURL = request.getRequestURL().toString();
		String rootURL = requestURL.substring(0, requestURL.length() - request.getRequestURI().length());
		return rootURL + request.getContextPath();
	}

}
