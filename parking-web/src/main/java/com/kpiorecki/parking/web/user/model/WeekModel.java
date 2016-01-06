package com.kpiorecki.parking.web.user.model;

import java.io.Serializable;

public class WeekModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private int week;
	private int columnSpan;

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getColumnSpan() {
		return columnSpan;
	}

	public void setColumnSpan(int columnSpan) {
		this.columnSpan = columnSpan;
	}

}
