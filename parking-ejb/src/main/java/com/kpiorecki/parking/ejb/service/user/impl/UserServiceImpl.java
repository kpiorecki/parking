package com.kpiorecki.parking.ejb.service.user.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dao.UserDao;
import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.UserGroup;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;

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
	public boolean isLoginAvailable(String login) {
		logger.info("checking if login={} is available", login);

		return userDao.isLoginAvailable(login);
	}

	@Override
	public UserDto findUser(String login) {
		logger.info("finding user={}", login);

		User user = userDao.load(login);
		return mapper.map(user, UserDto.class);
	}

	@Override
	public List<UserDto> findAllUsers() {
		logger.info("finding all users");

		List<User> users = userDao.findAll();

		return collectionMapper.mapToArrayList(users, UserDto.class);
	}
}
