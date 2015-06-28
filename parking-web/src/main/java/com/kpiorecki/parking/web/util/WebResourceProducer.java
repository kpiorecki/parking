package com.kpiorecki.parking.web.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Singleton;

@Singleton
public class WebResourceProducer {

	@Produces
	@RequestScoped
	FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	@Produces
	@RequestScoped
	ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}

}
