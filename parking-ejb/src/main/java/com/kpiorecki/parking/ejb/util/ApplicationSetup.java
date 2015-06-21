package com.kpiorecki.parking.ejb.util;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;

@Startup
@Singleton
public class ApplicationSetup {

	@Inject
	private Logger logger;

	@PostConstruct
	void setup() {
		logger.info("setting up VM default locale to English");
		Locale.setDefault(Locale.ENGLISH);
	}

}
