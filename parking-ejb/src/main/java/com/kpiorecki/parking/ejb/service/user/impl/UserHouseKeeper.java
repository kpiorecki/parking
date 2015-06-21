package com.kpiorecki.parking.ejb.service.user.impl;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.UserDao;

@Stateless
@Singleton
public class UserHouseKeeper {

	@Inject
	private Logger logger;

	@Inject
	private UserDao userDao;

	@Schedule(dayOfWeek = "*", hour = "3", minute = "15", persistent = false)
	public void deleteOutdatedNotActivatedUsers() {
		logger.info("deleting outdated not activated users");
		userDao.deleteOutdatedNotActivatedUsers();
	}
}
