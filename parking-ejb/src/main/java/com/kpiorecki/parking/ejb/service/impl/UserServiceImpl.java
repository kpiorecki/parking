package com.kpiorecki.parking.ejb.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.dozer.Mapper;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.User_;
import com.kpiorecki.parking.ejb.jpa.GenericDao;
import com.kpiorecki.parking.ejb.service.UserService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;

@Stateless
@Transactional
public class UserServiceImpl implements UserService {

	@Inject
	private Logger logger;

	@Inject
	private Mapper mapper;

	@Inject
	private CollectionMapper collectionMapper;

	@Inject
	private EntityManager entityManager;

	@Inject
	private GenericDao genericDao;

	@Override
	public void modifyUser(UserDto userDto) {
		logger.info("modifying user {}", userDto);

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
		logger.info("deleting user with login={}", login);

		User user = genericDao.findExistingEntity(User_.login, login);

		Query deleteRecordsQuery = entityManager.createNamedQuery("Record.deleteUserRecords");
		deleteRecordsQuery.setParameter("userId", user.getId());
		int deletedRecords = deleteRecordsQuery.executeUpdate();
		logger.info("deleted {} record(s) from user with login={}", deletedRecords, login);

		entityManager.remove(user);

		// need to flush and clear entity manager because of delete query above
		entityManager.flush();
		entityManager.clear();
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

	@Override
	public List<UserDto> findAllUsers() {
		logger.info("finding all users");

		List<User> users = genericDao.findAllEntities(User.class);

		return collectionMapper.mapToArrayList(users, UserDto.class);
	}
}
