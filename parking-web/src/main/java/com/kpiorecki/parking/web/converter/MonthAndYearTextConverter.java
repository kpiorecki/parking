package com.kpiorecki.parking.web.converter;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

@FacesConverter("monthAndYearTextConverter")
public class MonthAndYearTextConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM yyyy").withLocale(Locale.ENGLISH);
		if (value instanceof ReadableInstant) {
			return formatter.print((ReadableInstant) value);
		} else if (value instanceof ReadablePartial) {
			return formatter.print((ReadablePartial) value);
		}
		return null;
	}

}
