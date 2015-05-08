package com.kpiorecki.parking.web.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.util.DateFormatter;

@WebServlet(value = "/sample")
public class SampleServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger logger;

	@Inject
	@DateFormatter
	private DateTimeFormatter dateFormatter;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String dateString = new DateTime().toString(dateFormatter);
		String logMessage = "Sample invocation at " + dateString;

		logger.info(logMessage);

		resp.getWriter().println(logMessage);
	}
}