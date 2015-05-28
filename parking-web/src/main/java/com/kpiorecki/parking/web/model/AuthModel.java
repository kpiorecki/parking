package com.kpiorecki.parking.web.model;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class AuthModel {

	@Inject
	private FacesContext context;

	public String logout() {
		context.getExternalContext().invalidateSession();
		return "pretty:home?faces-redirect=true";
	}
}
