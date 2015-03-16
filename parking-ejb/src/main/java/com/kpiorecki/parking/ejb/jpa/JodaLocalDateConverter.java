package com.kpiorecki.parking.ejb.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

@Converter(autoApply = true)
public class JodaLocalDateConverter implements AttributeConverter<LocalDate, Long> {

	@Override
	public Long convertToDatabaseColumn(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}
		return localDate.toDateTimeAtStartOfDay().getMillis();
	}

	@Override
	public LocalDate convertToEntityAttribute(Long millis) {
		if (millis == null) {
			return null;
		}
		return new DateTime(millis.longValue()).toLocalDate();
	}
}
