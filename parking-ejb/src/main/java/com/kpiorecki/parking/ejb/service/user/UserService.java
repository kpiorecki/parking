package com.kpiorecki.parking.ejb.service.user;

import java.util.List;

import javax.ejb.Local;

import com.kpiorecki.parking.ejb.dto.UserDto;

@Local
public interface UserService {

	void addUser(UserDto user);

	void modifyUser(UserDto user);

	void deleteUser(String login);

	void registerUser(UserDto user, String activationUuid, String activationURL);

	boolean isLoginAvailable(String login);

	UserDto activateUser(String activationUuid);

	UserDto findUser(String login);

	List<UserDto> findAllUsers();

	void deleteOutdatedNotActivatedUsers();

	boolean requestResetPassword(String login, String resetPasswordUuid, String resetPasswordURL);

	String loadResetPasswordLogin(String resetPasswordUuid);

	boolean resetPassword(String login, String password);

}
