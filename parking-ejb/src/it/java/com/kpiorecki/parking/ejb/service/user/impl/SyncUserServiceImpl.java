package com.kpiorecki.parking.ejb.service.user.impl;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.enterprise.inject.Specializes;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;

/**
 * Specialized UserServiceImpl that handles user registration synchronously
 */
@Specializes
@Stateless
@Local(UserService.class)
public class SyncUserServiceImpl extends UserServiceImpl {

	@Override
	public void registerUser(UserDto userDto, String activationUuid, String activationURL) {
		super.registerUser(userDto, activationUuid, activationURL);
	}

}
