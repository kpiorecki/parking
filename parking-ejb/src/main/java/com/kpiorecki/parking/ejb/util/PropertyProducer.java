package com.kpiorecki.parking.ejb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;

@Singleton
public class PropertyProducer {

	@Inject
	private Logger logger;

	private Properties properties;

	@PostConstruct
	void loadProperties() {
		logger.info("loading configuration properties");
		properties = new Properties();

		try (InputStream inputStream = getClass().getResourceAsStream("/configuration.properties")) {
			properties.load(inputStream);
		} catch (IOException e) {
			String message = String.format("could not read properties - %s", e.getMessage());
			logger.error(message);
			throw new IllegalStateException(message, e);
		}
	}

	@Produces
	@Property("")
	String getStringProperty(InjectionPoint point) {
		String name = point.getAnnotated().getAnnotation(Property.class).value();
		String value = properties.getProperty(name);
		if (value == null) {
			String message = String.format("could not find property %s", name);
			logger.error(message);
			throw new IllegalStateException(message);
		}
		return value.trim();
	}

	@Produces
	@Property("")
	double getDoubleProperty(InjectionPoint point) {
		String stringValue = getStringProperty(point);
		return Double.parseDouble(stringValue);
	}

	@Produces
	@Property("")
	int getIntProperty(InjectionPoint point) {
		String stringValue = getStringProperty(point);
		return Integer.parseInt(stringValue);
	}
}
