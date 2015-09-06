package com.kpiorecki.parking.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.joda.time.DateTimeFieldType;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;

@FacesConverter("dayShortTextConverter")
public class DayShortTextConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Integer day = null;
		DateTimeFieldType fieldType = DateTimeFieldType.dayOfWeek();
		if (value instanceof ReadableInstant) {
			day = ((ReadableInstant) value).get(fieldType);
		} else if (value instanceof ReadablePartial) {
			day = ((ReadablePartial) value).get(fieldType);
		}

		return getDayShortText(day);
	}

	private String getDayShortText(Integer day) {
		if (day != null) {
			// did not find format that returns two character-length day names
			switch (day) {
			case 1:
				return "Mo";
			case 2:
				return "Tu";
			case 3:
				return "We";
			case 4:
				return "Th";
			case 5:
				return "Fr";
			case 6:
				return "Sa";
			case 7:
				return "Su";
			default:
				break;
			}
		}
		return null;
	}
}
