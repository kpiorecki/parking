package com.kpiorecki.parking.core.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.core.dao.UuidEntityDao;
import com.kpiorecki.parking.core.dto.UserDto;
import com.kpiorecki.parking.core.entity.User;
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
	private UuidEntityDao uuidEntityDao;

	@Override
	public void saveUser(UserDto userDto) {
		String message = String.format("saving user %s", userDto);
		logger.info(message);

		User user = uuidEntityDao.findByUuid(User.class, userDto.getUuid());
		if (user == null) {
			logger.debug("{} - creating new user", message);
			user = mapper.map(userDto, User.class);
		} else {
			logger.debug("{} - updating existing user", message);
			mapper.map(userDto, user);
		}
		entityManager.persist(user);
	}
}
