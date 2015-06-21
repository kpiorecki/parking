package com.kpiorecki.parking.ejb;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import com.kpiorecki.parking.ejb.service.user.impl.UserHouseKeeper;
import com.kpiorecki.parking.ejb.util.DataSourceMigrator;

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

	public static WebArchive createFullDeployment() {
		return createBasePersistenceDeployment().addPackages(true,
				Filters.exclude(DataSourceMigrator.class, UserHouseKeeper.class),
				Package.getPackage("com.kpiorecki.parking.ejb"));
	}
}
