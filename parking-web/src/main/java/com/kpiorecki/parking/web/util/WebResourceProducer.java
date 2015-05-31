package com.kpiorecki.parking.web.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

public class WebResourceProducer {

	@Produces
	@RequestScoped
	FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

}
