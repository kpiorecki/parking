package com.kpiorecki.parking.ejb;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class ArquillianFactory {

	private ArquillianFactory() {
		// no instances expected
	}

	public static WebArchive createBaseDeployment() {
		return ShrinkWrap.create(WebArchive.class).addAsWebInfResource("META-INF/beans.xml", "beans.xml");
	}

	public static WebArchive createBasePersistenceDeployment() {
		return createBaseDeployment().addAsResource("test-persistence.xml", "META-INF/persistence.xml");
	}
}
