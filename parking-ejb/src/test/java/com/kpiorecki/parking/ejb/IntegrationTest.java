package com.kpiorecki.parking.ejb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public abstract class IntegrationTest extends GreenMailTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBasePersistenceDeployment().addPackages(true,
				Package.getPackage("com.kpiorecki.parking.ejb"));
	}
}
