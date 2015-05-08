package com.kpiorecki.parking.web.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;

@WebServlet(value = "/add-user")
public class AddUserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserService userService;

	@Inject
	private Logger logger;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		long millis = +new DateTime().getMillis();

		UserDto user = new UserDto();
		user.setFirstName("firstname");
		user.setLastName("lastname");
		user.setLogin("login_" + millis);
		user.setEmail("user_" + millis + "@mail.com");

		userService.addUser(user);

		String message = "added user " + user;
		logger.info(message);

		resp.getWriter().println(message);
	}
}