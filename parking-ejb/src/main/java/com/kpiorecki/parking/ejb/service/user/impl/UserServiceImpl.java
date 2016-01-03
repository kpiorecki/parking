package com.kpiorecki.parking.ejb.service.user.impl;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import com.kpiorecki.parking.ejb.entity.User_;
import com.kpiorecki.parking.ejb.service.user.UserService;
import com.kpiorecki.parking.ejb.util.CollectionMapper;
import com.kpiorecki.parking.ejb.util.MailSender;
import com.kpiorecki.parking.ejb.util.Property;
import com.kpiorecki.parking.ejb.util.Role;

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
	@Property(value = "userService.activation.deadline.days", minIntValue = 0)
	private int activationDeadlineDays = 5;

	@Inject
	@Property(value = "userService.reset.password.hours", minIntValue = 1)
	private int resetPasswordHours = 2;

	@Override
	@RolesAllowed(Role.USER)
	public void modifyUser(UserDto userDto) {
		logger.info("modifying {}", userDto);

		User user = userDao.load(userDto.getLogin());
		mapper.map(userDto, user);

		userDao.save(user);
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void addUser(UserDto userDto) {
		logger.info("adding {}", userDto);

		User user = mapper.map(userDto, User.class);
		user.addGroup(UserGroup.USER);

		userDao.save(user);
	}

	@Override
	@RolesAllowed(Role.USER)
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

		user.setActivationDeadline(null);
		user.setActivationUuid(null);
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
	@RolesAllowed(Role.ADMIN)
	public List<UserDto> findAllUsers() {
		logger.info("finding all users");

		List<User> users = userDao.findAll();

		return collectionMapper.mapToArrayList(users, UserDto.class);
	}

	@Override
	@RolesAllowed(Role.ADMIN)
	public void deleteExpiredUsers() {
		logger.info("deleting expired not activated users");
		userDao.deleteExpiredUsers();
	}

	@Override
	public boolean requestResetPassword(String login, String resetPasswordUuid, String resetPasswordURL) {
		logger.info("requesting reset password for user={}, resetPasswordUuid={}, resetPasswordURL={}", login,
				resetPasswordUuid, resetPasswordURL);

		User user = userDao.find(login);
		if (user == null) {
			logger.warn("did not find user={} to request reset password", login);
			return false;
		}

		DateTime resetPasswordDeadline = new DateTime().plusHours(resetPasswordHours);
		user.setResetPasswordDeadline(resetPasswordDeadline);
		user.setResetPasswordUuid(resetPasswordUuid);
		userDao.save(user);

		mailSender.sendResetPasswordMail(user, resetPasswordURL, resetPasswordHours);

		return true;
	}

	@Override
	public boolean isResetPasswordValid(String resetPasswordUuid) {
		logger.info("validating resetPasswordUuid={}", resetPasswordUuid);

		return findResetPasswordUser(resetPasswordUuid) != null;
	}

	@Override
	public boolean resetPassword(String resetPasswordUuid, String password) {
		logger.info("resetting password for resetPasswordUuid={}", resetPasswordUuid);

		User user = findResetPasswordUser(resetPasswordUuid);
		if (user == null) {
			return false;
		}

		user.setPassword(password);
		user.setResetPasswordUuid(null);
		user.setResetPasswordDeadline(null);

		logger.info("saving user={} with reset password for resetPasswordUuid={}", user.getLogin(), resetPasswordUuid);
		userDao.save(user);

		return true;
	}

	private User findResetPasswordUser(String resetPasswordUuid) {
		List<User> users = userDao.find(User_.resetPasswordUuid, resetPasswordUuid);
		if (users.isEmpty()) {
			logger.warn("did not find user with resetPasswordUuid={}", resetPasswordUuid);
			return null;
		}

		User user = users.get(0);
		DateTime now = new DateTime();
		if (now.isAfter(user.getResetPasswordDeadline())) {
			logger.warn("resetPasswordUuid={} for user={} has expired", resetPasswordUuid, user.getLogin());
			return null;
		}

		return user;
	}
}
