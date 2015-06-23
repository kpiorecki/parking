package com.kpiorecki.parking.web.util;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import javax.inject.Singleton;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

@Singleton
public class WebResourceProducer {

	@Produces
	@RequestScoped
	FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	@Produces
	Encoder getURLEncoder() {
		return Base64.getUrlEncoder();
	}

	@Produces
	Decoder getURLDecoder() {
		return Base64.getUrlDecoder();
	}

}
