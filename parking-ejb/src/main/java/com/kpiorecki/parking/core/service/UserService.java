package com.kpiorecki.parking.core.service;

import java.util.List;

import javax.ejb.Local;

import com.kpiorecki.parking.core.dto.UserDto;

@Local
public interface UserService {

	void addUser(UserDto user);

	void modifyUser(UserDto user);

	void deleteUser(String login);

	UserDto findUser(String login);

	List<UserDto> findAllUsers();

}
