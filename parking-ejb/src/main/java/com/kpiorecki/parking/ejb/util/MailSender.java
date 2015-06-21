package com.kpiorecki.parking.ejb.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
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
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.User;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Stateless
public class MailSender {

	private static final String TPL_TITLE_USER = "titleUser";
	private static final String TPL_CONTENT_IMAGE = "contentImage";
	private static final String TPL_HEADER_IMAGE = "headerImage";

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

		Map<String, Object> globalParameters = new HashMap<>(templateParameters);
		globalParameters.put(TPL_TITLE_USER, getTitleUser(user));
		addImageSizeParameters(globalParameters, Image.PARKING_HEADER, TPL_HEADER_IMAGE);
		addImageSizeParameters(globalParameters, contentImage, TPL_CONTENT_IMAGE);

		MimeBodyPart bodyPart = createBodyPart(user, templateFile, globalParameters);
		MimeBodyPart headerImagePart = createImagePart(TPL_HEADER_IMAGE, Image.PARKING_HEADER);
		MimeBodyPart contentImagePart = createImagePart(TPL_CONTENT_IMAGE, contentImage);

		Multipart multipart = new MimeMultipart("related");
		multipart.addBodyPart(bodyPart);
		multipart.addBodyPart(headerImagePart);
		multipart.addBodyPart(contentImagePart);

		return multipart;
	}

	private MimeBodyPart createBodyPart(User user, String templateFile, Map<String, Object> globalParameters)
			throws Exception {
		Template template = templateConfiguration.getTemplate(templateFile);
		Writer writer = new StringWriter();
		template.process(globalParameters, writer);

		MimeBodyPart bodyPart = new MimeBodyPart();
		bodyPart.setText(writer.toString(), "UTF-8", "html");

		return bodyPart;
	}

	private MimeBodyPart createImagePart(String imageId, Image image) throws Exception {
		try (InputStream imageStream = getClass().getResourceAsStream(image.getFilePath())) {
			DataSource dataSource = new ByteArrayDataSource(imageStream, image.getMimeType());

			MimeBodyPart imagePart = new MimeBodyPart();
			imagePart.setContentID("<" + imageId + ">");
			imagePart.setDisposition(MimeBodyPart.INLINE);
			imagePart.setDataHandler(new DataHandler(dataSource));

			return imagePart;
		}
	}

	private void addImageSizeParameters(Map<String, Object> parameters, Image image, String imageId) {
		parameters.put(imageId + "W", image.getWidth());
		parameters.put(imageId + "H", image.getHeight());
	}

	private String getTitleUser(User user) {
		String firstName = user.getFirstName();
		if (StringUtils.isBlank(firstName)) {
			return user.getLogin();
		} else {
			return firstName;
		}
	}

}
