package com.kpiorecki.parking.web.user.view;

import javax.ejb.Stateless;
import javax.inject.Named;

import org.joda.time.LocalDate;

import com.kpiorecki.parking.web.user.model.DayModel;

@Named
@Stateless
public class BookingView {

	private static final String DAY_DEFAULT = "parking-booking-day";
	private static final String DAY_NUMBER = "parking-day-number";
	private static final String DAY_TEXT = "parking-day-text";
	private static final String DAY_CAPACITY_POSITIVE = "parking-capacity-pos";
	private static final String DAY_CAPACITY_NEGATIVE = "parking-capacity-neg";
	private static final String DAY_DISABLED = "parking-day-disabled";
	private static final String DAY_TODAY = "parking-day-today";

	private static final String WEEK_DEFAULT = "parking-booking-week";

	public String getDayNumberClass(DayModel model) {
		return getDayClass(model, DAY_NUMBER);
	}

	public String getDayTextClass(DayModel model) {
		return getDayClass(model, DAY_TEXT);
	}

	public String getDayCapacityClass(DayModel model) {
		if (model.isEnabled()) {
			int availableCapacity = model.getAvailableCapacity();
			if (availableCapacity > 0) {
				return getDayClass(model, DAY_CAPACITY_POSITIVE);
			} else if (availableCapacity < 0) {
				return getDayClass(model, DAY_CAPACITY_NEGATIVE);
			}
		}

		return getDayClass(model);
	}

	public String getDayStatusClass(DayModel model) {
		return getDayClass(model);
	}

	public String getWeekClass() {
		StringBuilder builder = new StringBuilder();
		appendCssClass(builder, WEEK_DEFAULT);

		return builder.toString();
	}

	private String getDayClass(DayModel model, String... nonDefaultClasses) {
		StringBuilder builder = new StringBuilder();
		appendCssClass(builder, DAY_DEFAULT);
		for (String clazz : nonDefaultClasses) {
			appendCssClass(builder, clazz);
		}

		if (!model.isEnabled()) {
			appendCssClass(builder, DAY_DISABLED);
		}

		LocalDate today = new LocalDate();
		if (today.isEqual(model.getDate())) {
			appendCssClass(builder, DAY_TODAY);
		}

		return builder.toString();
	}

	private void appendCssClass(StringBuilder builder, String clazz) {
		if (builder.length() > 0) {
			builder.append(' ');
		}
		builder.append(clazz);
	}

}
