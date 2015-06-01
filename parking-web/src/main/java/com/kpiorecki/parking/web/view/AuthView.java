package com.kpiorecki.parking.web.view;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@ViewScoped
public class AuthView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FacesContext context;

	private String username;
	private String password;
	private String originalURI;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	}

	public void checkLoggedIn() throws IOException {
		if (isLoggedIn()) {
			context.getExternalContext().redirect(getHomeURI());
		}
	}

	public void login() throws IOException {
		try {
			getHttpServletRequest().login(username, password);
			context.getExternalContext().redirect(originalURI);
		} catch (ServletException e) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid username or password", null);
			context.addMessage(null, message);
		}
	}

	public String logout() {
		context.getExternalContext().invalidateSession();
		return "pretty:index";
	}

	private boolean isLoggedIn() {
		return getHttpServletRequest().getRemoteUser() != null;
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
