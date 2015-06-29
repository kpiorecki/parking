package com.kpiorecki.parking.ejb.service.user.impl;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.UserGroup;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;
import com.kpiorecki.parking.ejb.util.MailSender;
import com.kpiorecki.parking.ejb.util.Property;

@Stateless
public class UserServiceImpl implements UserService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private CollectionMapper collectionMapper;

	@Inject
	private UserDao userDao;

	@Inject
	private MailSender mailSender;

	@Inject
	@Property("userService.activation.deadline.days")
	private int activationDeadlineDays;

	@Override
	public void modifyUser(UserDto userDto) {
		logger.info("modifying user {}", userDto);

		User user = userDao.load(userDto.getLogin());
		mapper.map(userDto, user);

		userDao.save(user);
	}

	@Override
	public void addUser(UserDto userDto) {
		logger.info("adding user {}", userDto);

		User user = mapper.map(userDto, User.class);
		user.addGroup(UserGroup.USER);

		userDao.save(user);
	}

	@Override
	public void deleteUser(String login) {
		logger.info("deleting user={}", login);

		userDao.delete(login);
	}

	@Override
	public void registerUser(UserDto userDto, String activationUuid, String activationURL) {
		logger.info("registering user {}, activationUuid={}, activationURL={}", userDto, activationUuid, activationURL);

		LocalDate today = new DateTime().toLocalDate();
		DateTime deadline = today.plusDays(activationDeadlineDays + 1).toDateTimeAtStartOfDay().minusSeconds(1);

		User user = mapper.map(userDto, User.class);
		user.addGroup(UserGroup.USER);
		user.setActivationUuid(activationUuid);
		user.setActivationDeadline(deadline);
		userDao.save(user);

		mailSender.sendRegisterMail(user, activationURL, deadline);
	}

	@Override
	public boolean isLoginAvailable(String login) {
		logger.info("checking if login={} is available", login);

		return userDao.isLoginAvailable(login);
	}

	@Override
	public UserDto activateUser(String activationUuid) {
		logger.info("activating user with activationUuid={}", activationUuid);

		User user = userDao.findUserToActivate(activationUuid);
		if (user == null) {
			logger.warn("did not find user with activationUuid={}", activationUuid);
			return null;
		}

		user.setActivationUuid(null);
		user.setActivationDeadline(null);
		userDao.save(user);

		logger.info("activated user={} with activationUuid={}", user.getLogin(), activationUuid);

		return mapper.map(user, UserDto.class);
	}

	@Override
	public UserDto findUser(String login) {
		logger.info("finding user={}", login);

		User user = userDao.find(login);
		if (user == null) {
			return null;
		}

		return mapper.map(user, UserDto.class);
	}

	@Override
	public List<UserDto> findAllUsers() {
		logger.info("finding all users");

		List<User> users = userDao.findAll();

		return collectionMapper.mapToArrayList(users, UserDto.class);
	}

	@Schedule(dayOfWeek = "*", hour = "3", minute = "15", persistent = false)
	public void deleteOutdatedNotActivatedUsers() {
		logger.info("deleting outdated not activated users");
		userDao.deleteOutdatedNotActivatedUsers();
	}
}
