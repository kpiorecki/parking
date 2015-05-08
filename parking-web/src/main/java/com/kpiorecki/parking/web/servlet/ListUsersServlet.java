package com.kpiorecki.parking.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.dto.UserDto;
import com.kpiorecki.parking.ejb.service.user.UserService;

@WebServlet(value = "/list-users")
public class ListUsersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserService userService;

	@Inject
	private Logger logger;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<UserDto> allUsers = userService.findAllUsers();
		StringBuilder sb = new StringBuilder();
		sb.append("user list:");
		int i = 1;
		for (UserDto userDto : allUsers) {
			sb.append("\n").append(i++).append(". ").append(userDto);
		}

		String message = sb.toString();
		logger.info(message);

		resp.getWriter().println(message	);
	}
}