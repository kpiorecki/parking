package com.kpiorecki.parking.ejb.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.joda.time.DateTime;

@Converter
public class DateOnlyConverter implements AttributeConverter<DateTime, Long> {

	@Override
	public Long convertToDatabaseColumn(DateTime attribute) {
		if (attribute == null) {
			return null;
		}

		return attribute.withMillisOfDay(0).getMillis();
	}

	@Override
	public DateTime convertToEntityAttribute(Long dbData) {
		if (dbData == null) {
			return null;
		}

		return new DateTime(dbData.longValue());
	}
}
