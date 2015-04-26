package com.kpiorecki.parking.ejb;

import org.junit.After;
import org.junit.Before;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public abstract class GreenMailTest {

	private static final int SMTP_TEST_PORT = 3025;

	private GreenMail greenMail;

	@Before
	public void startGreenMail() throws Exception {
		greenMail = new GreenMail(new ServerSetup(SMTP_TEST_PORT, null, "smtp"));
		greenMail.start();
	}

	@After
	public void stopGreenMail() throws Exception {
		greenMail.stop();
	}

}
