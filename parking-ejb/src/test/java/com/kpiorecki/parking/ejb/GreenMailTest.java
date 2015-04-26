package com.kpiorecki.parking.ejb;

import org.junit.After;
import org.junit.Before;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

public abstract class GreenMailTest {

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

}
