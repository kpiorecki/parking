package com.kpiorecki.parking.ejb.service.user;

import java.util.List;

import javax.ejb.Local;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.entity.UserGroup;
import com.kpiorecki.parking.ejb.service.user.impl.UserPasswordEncoder;

@Local
public interface UserService {

	/**
	 * Adds new user and assigns it to {@link UserGroup#USER} group. The user account is activated and ready to use.
	 */
	void addUser(UserDto user);

	/**
	 * Modifies given user.
	 */
	void modifyUser(UserDto user);

	/**
	 * Deletes the user. The account is deactivated, but its history (e.g. bookings) remains.
	 */
	void deleteUser(String login);

	/**
	 * Creates new user and sends the mail with activation link to {@link UserDto#getEmail()} address.
	 * 
	 * @param activationUuid
	 *            Unique activation identifier that will be used to activate the account.
	 * @param activationURL
	 *            Link sent in the mail to the user. Needs to contain the information about activationUuid.
	 */
	void registerUser(UserDto user, String activationUuid, String activationURL);

	/**
	 * Checks whether given login is available and can be used to create new user.
	 */
	boolean isLoginAvailable(String login);

	/**
	 * Activates registered new user - the account is ready to use.
	 * 
	 * @return Activated user or <code>null</code> if activation failed (invalid or expired activationUuid).
	 */
	UserDto activateUser(String activationUuid);

	/**
	 * Finds active user.
	 * 
	 * @return Found user or <code>null</code> if not found.
	 */
	UserDto findUser(String login);

	/**
	 * Finds all active users.
	 */
	List<UserDto> findAllUsers();

	/**
	 * Deletes registered but not activated users whose activationUuid has expired.
	 */
	void deleteExpiredUsers();

	/**
	 * Sends reset password link to given user's mail address.
	 * 
	 * @param resetPasswordUuid
	 *            Unique reset password identifier that will be used authenticate the user
	 * @param resetPasswordURL
	 *            Link sent in the mail to the user. Needs to contain the information about resetPasswordUuid.
	 */
	boolean requestResetPassword(String login, String resetPasswordUuid, String resetPasswordURL);

	/**
	 * Validates reset password identifier.
	 * 
	 * @return <code>false</code> if resetPasswordUuid is invalid or expired, <code>true</code> otherwise.
	 */
	boolean isResetPasswordValid(String resetPasswordUuid);

	/**
	 * Resets password for given reset password identifier.
	 * 
	 * @param password
	 *            Password encoded by {@link UserPasswordEncoder#encode(String)} method.
	 * 
	 * @return <code>true</code> if operation succeeded, <code>false</code> otherwise.
	 */
	boolean resetPassword(String resetPasswordUuid, String password);

}
