package com.kpiorecki.parking.ejb.service.user.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.UserGroup;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;
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

		DateTime deadline = new DateTime().toLocalDate().plusDays(activationDeadlineDays + 1).toDateTimeAtStartOfDay();

		User user = mapper.map(userDto, User.class);
		user.addGroup(UserGroup.USER);
		user.setActivationUuid(activationUuid);
		user.setActivationDeadline(deadline);
		userDao.save(user);
		
		// TODO mail sender
	}

	@Override
	public void activateUser(String activationUuid) {
		logger.info("activating user with activationUuid={}", activationUuid);
		// TODO
	}

	@Override
	public boolean isLoginAvailable(String login) {
		logger.info("checking if login={} is available", login);

		return userDao.isLoginAvailable(login);
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
}
