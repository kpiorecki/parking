package com.kpiorecki.parking.ejb.mail;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.exception.DomainException;

@Stateless
public class MailSender {

	@Inject
	private Logger logger;

	@Resource(name = "java:comp/env/mail/session")
	private Session session;

	public void send(User user, String subject, String text) {
		String logMessage = String.format("sending mail to user=%s", user.getLogin());
		logger.info(logMessage);

		try {
			String personal = String.format("%s %s", user.getFirstName(), user.getLastName());
			InternetAddress address = new InternetAddress(user.getEmail(), personal);

			Message message = new MimeMessage(session);
			message.setSubject(subject);
			message.setRecipient(RecipientType.TO, address);
			message.setText(text);

			Transport.send(message);
		} catch (Exception e) {
			String warnMessage = String.format("%s - failed", logMessage);
			logger.warn(warnMessage, e);
			throw new DomainException(warnMessage, e);
		}
	}
}
