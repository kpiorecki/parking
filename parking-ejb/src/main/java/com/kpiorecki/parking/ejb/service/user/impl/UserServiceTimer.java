package com.kpiorecki.parking.ejb.service.user.impl;

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

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.util.Property;
import com.kpiorecki.parking.ejb.util.Role;

@Singleton
@Startup
@RunAs(Role.ADMIN)
public class UserServiceTimer {

	@Inject
	private Logger logger;

	@Inject
	private UserService userService;

	@Resource
	private TimerService timerService;

	@Inject
	@Property(value = "userServiceTimer.expression.dayOfWeek")
	private String dayOfweek;

	@Inject
	@Property(value = "userServiceTimer.expression.hour")
	private String hour;

	@Inject
	@Property(value = "userServiceTimer.expression.minute")
	private String minute;

	@PostConstruct
	void createTimer() {
		ScheduleExpression expression = new ScheduleExpression().dayOfWeek(dayOfweek).hour(hour).minute(minute);

		TimerConfig config = new TimerConfig();
		config.setPersistent(false);

		logger.info("creating user service timer with {} and {}", expression, config);
		timerService.createCalendarTimer(expression, config);
	}

	@Timeout
	void onTimeout() {
		logger.info("triggering user service 'deleteOutdatedNotActivatedUsers' method");
		userService.deleteOutdatedNotActivatedUsers();
	}
}