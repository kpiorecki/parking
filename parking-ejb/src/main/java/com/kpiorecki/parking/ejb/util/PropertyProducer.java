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
			throw new IllegalStateException(String.format("could not read properties - %s", e.getMessage()), e);
		}
	}

	@Produces
	@Property("")
	String getStringProperty(InjectionPoint point) {
		String name = getAnnotation(point).value();
		String value = properties.getProperty(name);
		if (value == null) {
			throw new IllegalStateException(String.format("could not find property %s", name));
		}
		return value.trim();
	}

	@Produces
	@Property("")
	double getDoubleProperty(InjectionPoint point) {
		String stringValue = getStringProperty(point);
		double value = Double.parseDouble(stringValue);

		Property property = getAnnotation(point);
		validateRange(property.value(), value, property.minDoubleValue(), property.maxDoubleValue());

		return value;
	}

	@Produces
	@Property("")
	int getIntProperty(InjectionPoint point) {
		String stringValue = getStringProperty(point);
		int value = Integer.parseInt(stringValue);

		Property property = getAnnotation(point);
		validateRange(property.value(), value, property.minIntValue(), property.maxIntValue());

		return value;
	}

	private <T extends Number> void validateRange(String name, T value, T minValue, T maxValue) {
		double doubleValue = value.doubleValue();
		double doubleMinValue = minValue.doubleValue();
		double doubleMaxValue = maxValue.doubleValue();

		if (doubleMinValue > doubleMaxValue) {
			throw new IllegalStateException(String.format(
					"property %s minimum value %s is bigger than maximum value %s", name, minValue, maxValue));
		}

		if (doubleValue < doubleMinValue) {
			throw new IllegalStateException(String.format("property %s value %s is smaller than minimum value %s",
					name, value, minValue));
		}

		if (doubleValue > doubleMaxValue) {
			throw new IllegalStateException(String.format("property %s value %s is bigger than maximum value %s", name,
					value, maxValue));
		}
	}

	private Property getAnnotation(InjectionPoint point) {
		return point.getAnnotated().getAnnotation(Property.class);
	}
}
