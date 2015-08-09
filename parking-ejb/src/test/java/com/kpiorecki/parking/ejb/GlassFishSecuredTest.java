package com.kpiorecki.parking.ejb;

import javax.annotation.security.RolesAllowed;
import javax.naming.InitialContext;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandResult.ExitStatus;
import org.glassfish.embeddable.CommandRunner;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kpiorecki.parking.ejb.arquillian.AfterDeploy;
import com.kpiorecki.parking.ejb.arquillian.BeforeUnDeploy;
import com.sun.enterprise.security.auth.realm.file.FileRealm;
import com.sun.enterprise.security.ee.auth.login.ProgrammaticLogin;

public abstract class GlassFishSecuredTest {

	private static final Logger logger = LoggerFactory.getLogger(GlassFishSecuredTest.class);
	private static final String realmName = "arquillian-file-realm";
	private static final String keyFile = "src/test/resources/test-keyfile";
	private static final String keyFileUser = "arquillian";
	private static final char[] keyFilePassword = new char[0];

	private static CommandRunner commandRunner;

	private ProgrammaticLogin programmaticLogin;

	/**
	 * Creates new file realm using 'src/test/resources/test-keyfile' file
	 */
	@AfterDeploy
	public static void createAuthFileRealm() throws Exception {
		/*
		 * embedded GlassFish server does not have 'login.conf' configured in default domain.xml (but the file exists in
		 * server directory). Pointing 'java.security.auth.login.config' environment variable to that file does the job.
		 */
		System.setProperty("java.security.auth.login.config", "${com.sun.aas.instanceRoot}/config/login.conf");

		InitialContext context = new InitialContext();
		commandRunner = (CommandRunner) context.lookup(CommandRunner.class.getName());
		if (commandRunner == null) {
			throw new RuntimeException("could not lookup CommandRunner instance");
		}

		String command = "create-auth-realm";
		CommandResult commandResult = commandRunner.run(command, "--classname=" + FileRealm.class.getName(),
				"--property=jaas-context=fileRealm:file=" + keyFile, realmName);
		validateCommandResult(command, commandResult);
	}

	/**
	 * Deletes previously created file realm
	 */
	@BeforeUnDeploy
	public static void deleteAuthFileRealm() {
		try {
			String command = "delete-auth-realm";
			CommandResult commandResult = commandRunner.run(command, realmName);
			validateCommandResult(command, commandResult);
		} finally {
			commandRunner = null;
		}
	}

	private static void validateCommandResult(String command, CommandResult commandResult) {
		ExitStatus status = commandResult.getExitStatus();
		if (status != ExitStatus.SUCCESS) {
			throw new RuntimeException(String.format("%s returned %s - %s", command, status, commandResult.getOutput()));
		}
	}

	/**
	 * Logs in test user. It enables invoking service methods wrapped with declarative authorization annotations (like
	 * {@link RolesAllowed})
	 */
	@Before
	public void loginTestUser() throws Exception {
		programmaticLogin = new ProgrammaticLogin();
		Boolean loggedIn = programmaticLogin.login(keyFileUser, keyFilePassword, realmName, true);
		if (loggedIn) {
			logger.debug("programmatically logged in user {}", keyFileUser);
		} else {
			throw new RuntimeException(String.format("programmatic login user %s failed", keyFileUser));
		}
	}

	/**
	 * Logs out test user
	 */
	@After
	public void logoutTestUser() {
		try {
			Boolean loggedOut = programmaticLogin.logout();
			if (loggedOut) {
				logger.debug("programmatically logged out current user");
			} else {
				throw new RuntimeException("current user programmatic logout failed");
			}
		} finally {
			programmaticLogin = null;
		}
	}
}
