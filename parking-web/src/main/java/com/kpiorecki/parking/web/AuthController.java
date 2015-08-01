package com.kpiorecki.parking.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;

@Named
@ViewScoped
public class AuthController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	private FacesContext context;

	@Inject
	private ExternalContext externalContext;

	@Inject
	private UserService userService;

	private String login;
	private String password;
	private String redirectURL;

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

	@PostConstruct
	public void init() {
		Map<String, Object> requestMap = externalContext.getRequestMap();
		redirectURL = (String) requestMap.get(RequestDispatcher.FORWARD_REQUEST_URI);
		if (!isSecuredURL(redirectURL)) {
			String loggedHomeURL = getLoggedHomeURL();
			logger.info("initialized with redirectURL {} instead of not secured {}", loggedHomeURL, redirectURL);
			redirectURL = loggedHomeURL;
		} else {
			String originalQuery = (String) requestMap.get(RequestDispatcher.FORWARD_QUERY_STRING);
			if (originalQuery != null) {
				redirectURL += "?" + originalQuery;
			}
			logger.info("initialized with redirectURL {}", redirectURL);
		}
	}

	public void redirectLoggedIn() throws IOException {
		String login = externalContext.getRemoteUser();
		if (login != null) {
			logger.info("user {} is logged in - redirecting to logged home", login);
			externalContext.redirect(getLoggedHomeURL());
		}
	}

	public void login() throws IOException {
		try {
			UserDto user = userService.findUser(login);
			if (user == null) {
				onLoginFailed();
				return;
			}

			// authenticating user will throw ServletException if failed
			logger.info("logging in user {}", login);
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			request.login(login, password);

			// authentication succeeded
			externalContext.redirect(redirectURL);
		} catch (ServletException e) {
			onLoginFailed();
		}
	}

	public void logout() throws IOException {
		logger.info("logging out current user");
		externalContext.invalidateSession();

		// redirect to home page
		externalContext.redirect(externalContext.getRequestContextPath());
	}

	private void onLoginFailed() {
		logger.info("logging in user {} failed", login);
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid login or password", null);
		context.addMessage(null, message);
	}

	private String getLoggedHomeURL() {
		return externalContext.getRequestContextPath() + "/user";
	}

	private boolean isSecuredURL(String url) {
		for (String prefix : getSecuredURLPrefixes()) {
			String securedURL = externalContext.getRequestContextPath() + prefix;
			if (url.toLowerCase().startsWith(securedURL)) {
				return true;
			}
		}
		return false;
	}

	private List<String> getSecuredURLPrefixes() {
		List<String> prefixes = new LinkedList<>();
		prefixes.add("/user");
		prefixes.add("/admin");

		return prefixes;
	}

}
