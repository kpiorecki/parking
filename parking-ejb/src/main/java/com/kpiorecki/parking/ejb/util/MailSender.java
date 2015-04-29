package com.kpiorecki.parking.ejb.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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

	public void send(User user, String subject, String templateFile, Map<String, Object> templateParameters,
			Image contentImage) {
		String logMessage = String.format("sending mail to user=%s", user.getLogin());
		logger.info(logMessage);

		try {
			String personal = String.format("%s %s", user.getFirstName(), user.getLastName());
			InternetAddress address = new InternetAddress(user.getEmail(), personal);
			Multipart content = createContent(user, templateFile, templateParameters, contentImage);

			MimeMessage message = new MimeMessage(mailSession);
			message.setContent(content);
			message.setSubject(subject);
			message.setRecipient(RecipientType.TO, address);
			message.setFrom();

			Transport.send(message);
		} catch (Exception e) {
			String warnMessage = String.format("%s - failed", logMessage);
			logger.warn(warnMessage, e);
			throw new DomainException(warnMessage, e);
		}
	}

	private Multipart createContent(User user, String templateFile, Map<String, Object> templateParameters,
			Image contentImage) throws Exception {
		logger.info("creating mail content using template={} and image={}", templateFile, contentImage);

		MimeBodyPart bodyPart = createBodyPart(user, templateFile, templateParameters);
		MimeBodyPart headerImagePart = createImagePart("header-image", Image.PARKING_HEADER);
		MimeBodyPart contentImagePart = createImagePart("content-image", contentImage);

		Multipart multipart = new MimeMultipart("related");
		multipart.addBodyPart(bodyPart);
		multipart.addBodyPart(headerImagePart);
		multipart.addBodyPart(contentImagePart);

		return multipart;
	}

	private MimeBodyPart createBodyPart(User user, String templateFile, Map<String, Object> templateParameters)
			throws Exception {
		Map<String, Object> globalParameters = new HashMap<>(templateParameters);
		globalParameters.put("titleUser", user);

		Template template = templateConfiguration.getTemplate(templateFile);
		Writer writer = new StringWriter();
		template.process(globalParameters, writer);

		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText(writer.toString(), "UTF-8", "html");

		return bodyPart;
	}

	private MimeBodyPart createImagePart(String imageId, Image image) throws Exception {
		MimeBodyPart imagePart = new MimeBodyPart();
		imagePart.attachFile(image.getFilePath());
		imagePart.setContentID("<" + imageId + ">");
		imagePart.setDisposition(MimeBodyPart.INLINE);

		return imagePart;
	}

}
