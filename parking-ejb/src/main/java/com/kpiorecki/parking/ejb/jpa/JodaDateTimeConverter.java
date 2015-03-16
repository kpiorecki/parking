package com.kpiorecki.parking.ejb.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.joda.time.DateTime;

@Converter(autoApply = true)
public class JodaDateTimeConverter implements AttributeConverter<DateTime, Long> {

	@Override
	public Long convertToDatabaseColumn(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.getMillis();
	}

	@Override
	public DateTime convertToEntityAttribute(Long millis) {
		if (millis == null) {
			return null;
		}
		return new DateTime(millis.longValue());
	}

}
