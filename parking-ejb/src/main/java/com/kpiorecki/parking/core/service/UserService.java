package com.kpiorecki.parking.core.service;

import javax.ejb.Local;

import com.kpiorecki.parking.core.dto.UserDto;

@Local
public interface UserService {

	void saveUser(UserDto user);

}
