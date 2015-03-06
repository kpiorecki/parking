package com.kpiorecki.parking.core.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.core.dto.UserDto;
import com.kpiorecki.parking.core.entity.User;
import com.kpiorecki.parking.core.entity.User_;
import com.kpiorecki.parking.core.service.UserService;

@Stateless
public class UserServiceImpl implements UserService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private EntityManager entityManager;

	@Inject
	private GenericDao genericDao;

	@Override
	public void modifyUser(UserDto userDto) {
		String message = String.format("modifying user %s", userDto);
		logger.info(message);

		User user = genericDao.findExistingEntity(User_.login, userDto.getLogin());
		mapper.map(userDto, user);

		entityManager.persist(user);
	}

	@Override
	public void addUser(UserDto userDto) {
		logger.info("adding user {}", userDto);

		User user = mapper.map(userDto, User.class);

		entityManager.persist(user);
	}

	@Override
	public void deleteUser(String login) {
		String message = String.format("deleting user with login=%s", login);
		logger.info(message);

		User user = genericDao.findExistingEntity(User_.login, login);

		entityManager.remove(user);
	}

	@Override
	public UserDto findUser(String login) {
		logger.info("finding user with login={}", login);

		User user = genericDao.findEntity(User_.login, login);
		if (user == null) {
			return null;
		} else {
			return mapper.map(user, UserDto.class);
		}
	}
}
