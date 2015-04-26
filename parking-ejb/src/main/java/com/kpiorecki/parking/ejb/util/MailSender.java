package com.kpiorecki.parking.ejb.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.User;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Stateless
public class MailSender {

	@Inject
	private Logger logger;

	@Inject
	private Configuration templateConfiguration;

	@Inject
	private Session mailSession;

	public void send(User user, String subject, String templateFile, Map<String, Object> parameters) {
		String logMessage = String.format("sending mail to user=%s", user.getLogin());
		logger.info(logMessage);

		try {
			String personal = String.format("%s %s", user.getFirstName(), user.getLastName());
			InternetAddress address = new InternetAddress(user.getEmail(), personal);
			String content = createContent(templateFile, parameters);

			MimeMessage message = new MimeMessage(mailSession);
			message.setContent(content, "text/html; charset=utf-8");
			message.setSubject(subject);
			message.setRecipient(RecipientType.TO, address);

			Transport.send(message);
		} catch (Exception e) {
			String warnMessage = String.format("%s - failed", logMessage);
			logger.warn(warnMessage, e);
			throw new DomainException(warnMessage, e);
		}
	}

	private String createContent(String templateFile, Map<String, Object> parameters) throws Exception {
		logger.info("creating mail content using template={}", templateFile);

		Template template = templateConfiguration.getTemplate(templateFile);
		Writer writer = new StringWriter();
		template.process(parameters, writer);

		return writer.toString();
	}

}
