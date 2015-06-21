package com.kpiorecki.parking.ejb;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

public abstract class GreenMailTest {

	@Inject
	private Logger logger;

	protected GreenMail greenMail;

	@Before
	public void startGreenMail() throws Exception {
		greenMail = new GreenMail(ServerSetupTest.SMTP);
		greenMail.start();
	}

	@After
	public void stopGreenMail() throws Exception {
		greenMail.stop();
	}

	protected void assertOneMailSent() {
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertTrue(receivedMessages.length == 1);

		MimeMessage message = receivedMessages[0];
		logger.info("sent one mail {}", getMailLog(message));
	}

	protected String getMailLog(MimeMessage message) {
		try {
			return String.format("from=%s, to=%s, subject=%s, body:\n%s", message.getFrom(),
					message.getRecipients(RecipientType.TO), message.getSubject(), GreenMailUtil.getBody(message));
		} catch (MessagingException e) {
			logger.error("could not get mail log - {}", e.getCause());
			throw new RuntimeException(e);
		}
	}

}
