package com.kpiorecki.parking.ejb.jpa;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class DateConverterTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(JavaArchive.class).addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml").addClass(DateEntity.class)
				.addPackage(Package.getPackage("com.kpiorecki.parking.ejb.util"))
				.addPackage(Package.getPackage("com.kpiorecki.parking.ejb.jpa"));
	}
}
