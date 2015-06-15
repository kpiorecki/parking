package com.kpiorecki.parking.web.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;

@ManagedBean
@ViewScoped
public class AuthController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private FacesContext context;

	@Inject
	private UserService userService;

	@ManagedProperty(value = "#{userController}")
	private UserController userController;

	private String login;
	private String password;
	private String originalURI;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserController(UserController userController) {
		this.userController = userController;
	}

	@PostConstruct
	public void init() {
		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		originalURI = (String) requestMap.get(RequestDispatcher.FORWARD_REQUEST_URI);
		if (getLoginURI().equalsIgnoreCase(originalURI)) {
			originalURI = getHomeURI();
		} else {
			String originalQuery = (String) requestMap.get(RequestDispatcher.FORWARD_QUERY_STRING);
			if (originalQuery != null) {
				originalURI += "?" + originalQuery;
			}
		}
		logger.info("initialized with originalURI {}", originalURI);
	}

	public void forwardLoggedIn() throws IOException {
		if (userController.isLoggedIn()) {
			logger.info("forwarding request to home");
			context.getExternalContext().redirect(getHomeURI());
		}
	}

	public void login() throws IOException {
		try {
			logger.info("logging in user {}", login);
			getHttpServletRequest().login(login, password);

			UserDto user = userService.findUser(login);
			userController.setLoggedInUser(user);

			context.getExternalContext().redirect(originalURI);
		} catch (ServletException e) {
			logger.info("logging in user {} failed", login);
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login or password", null);
			context.addMessage(null, message);
		}
	}

	public String logout() {
		logger.info("logging out user {}", userController.getLogin());
		context.getExternalContext().invalidateSession();
		return "pretty:index";
	}

	private String getHomeURI() {
		return context.getExternalContext().getRequestContextPath() + "/user";
	}

	private String getLoginURI() {
		return context.getExternalContext().getRequestContextPath() + "/login";
	}

	private HttpServletRequest getHttpServletRequest() {
		return (HttpServletRequest) context.getExternalContext().getRequest();
	}

}
