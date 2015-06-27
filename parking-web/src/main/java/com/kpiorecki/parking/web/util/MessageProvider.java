package com.kpiorecki.parking.web.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class MessageProvider {

	@Inject
	private Logger logger;

	@Inject
	private FacesContext context;

	private ResourceBundle messageBundle;

	@PostConstruct
	public void initialize() {
		messageBundle = context.getApplication().getResourceBundle(context, "messages");
	}

	public String getMessage(String key) {
		try {
			return messageBundle.getString(key);
		} catch (MissingResourceException e) {
			logger.warn("did not find message with key={}, returning null", key);
			return null;
		}
	}

	public boolean containsMessage(String key) {
		return messageBundle.containsKey(key);
	}

}
