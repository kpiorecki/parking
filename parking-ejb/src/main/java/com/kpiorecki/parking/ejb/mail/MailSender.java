package com.kpiorecki.parking.ejb.mail;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Session;

import org.slf4j.Logger;

@Stateless
public class MailSender {

	@Inject
	private Logger logger;

	@Resource(name = "java:comp/env/mail/session")
	private Session session;

}
