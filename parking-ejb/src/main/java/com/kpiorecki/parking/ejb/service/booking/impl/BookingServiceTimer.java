package com.kpiorecki.parking.ejb.service.booking.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.RunAs;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.joda.time.LocalTime;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.service.booking.BookingService;
import com.kpiorecki.parking.ejb.util.Property;
import com.kpiorecki.parking.ejb.util.Role;

@Singleton
@Startup
@RunAs(Role.ADMIN)
public class BookingServiceTimer {

	@Inject
	private Logger logger;

	@Inject
	private BookingService bookingService;

	@Inject
	private BookingStatusPolicy statusPolicy;

	@Resource
	private TimerService timerService;

	@Inject
	@Property(value = "bookingServiceTimer.expression.minuteOffset", minIntValue = 0)
	private int minuteOffset = 10;

	@PostConstruct
	void createTimer() {
		LocalTime time = statusPolicy.getLockTime().plusMinutes(minuteOffset);

		int hour = time.getHourOfDay();
		int minute = time.getMinuteOfHour();

		ScheduleExpression expression = new ScheduleExpression().hour(hour).minute(minute);

		TimerConfig config = new TimerConfig();
		config.setPersistent(false);

		logger.info("creating booking service timer with {} and {}", expression, config);
		timerService.createCalendarTimer(expression, config);
	}

	@Timeout
	void onTimeout() {
		logger.info("triggering booking service 'lockAccordingToPolicy' method");
		bookingService.lockAccordingToPolicy();
	}
}