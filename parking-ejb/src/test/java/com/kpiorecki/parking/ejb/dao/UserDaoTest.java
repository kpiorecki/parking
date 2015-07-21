package com.kpiorecki.parking.ejb.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.TestUtilities;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.jpa.JodaDateTimeConverter;
import com.kpiorecki.parking.ejb.util.ResourceProducer;
import com.kpiorecki.parking.ejb.util.UuidGenerator;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class UserDaoTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBasePersistenceDeployment().addClasses(UserDao.class, ParkingDao.class,
				BookingDao.class, TestUtilities.class, ResourceProducer.class, UuidGenerator.class, User.class,
				JodaDateTimeConverter.class);
	}

	@Inject
	private EntityManager entityManager;

	@Inject
	private UserDao userDao;

	@Inject
	private TestUtilities testUtilities;

	@Test
	public void shouldDeleteOutdatedNotActivatedUsers() {
		// given
		testUtilities.persistUser("regularUser");

		User naUser1 = testUtilities.createUser("notActiveUser1");
		naUser1.setActivationDeadline(new DateTime().plusDays(-1));
		naUser1.setActivationUuid("uuid1");
		entityManager.persist(naUser1);

		User naUser2 = testUtilities.createUser("notActiveUser2");
		naUser2.setActivationDeadline(new DateTime().plusDays(1));
		naUser2.setActivationUuid("uuid2");
		entityManager.persist(naUser2);

		entityManager.flush();

		// when
		userDao.deleteOutdatedNotActivatedUsers();

		// then
		assertNotNull(entityManager.find(User.class, "regularUser"));
		assertNotNull(entityManager.find(User.class, "notActiveUser1"));
		assertNull(entityManager.find(User.class, "notActiveUser2"));
	}
}
