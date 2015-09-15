package com.kpiorecki.parking.ejb.service.calendar.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.service.calendar.CalendarService;

@Stateless
public class CalendarServiceImpl implements CalendarService {

	@Inject
	private Logger logger;

}
