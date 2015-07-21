package com.kpiorecki.parking.ejb.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.Archive;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.kpiorecki.parking.ejb.ArquillianFactory;
import com.kpiorecki.parking.ejb.util.ResourceProducer;

@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class DateConverterTest {

	@Deployment
	public static Archive<?> createDeployment() {
		return ArquillianFactory.createBasePersistenceDeployment().addClasses(DateEntity.class,
				JodaDateTimeConverter.class, JodaLocalDateConverter.class, ResourceProducer.class);
	}

	@Inject
	private EntityManager entityManager;

	@Test
	public void shouldFindByLocalDate() {
		// given
		DateEntity entity = addDateEntity();

		// when
		TypedQuery<DateEntity> findQuery = entityManager.createQuery("select d from DateEntity d where d.date = :date",
				DateEntity.class);
		findQuery.setParameter("date", entity.getDate());
		DateEntity foundEntity = findQuery.getSingleResult();

		// then
		assertNotNull(foundEntity);
		assertEquals(entity.getDate(), foundEntity.getDate());
		assertEquals(entity.getDateTime(), foundEntity.getDateTime());
	}

	@Test
	public void shouldFindByUTCDateTime() {
		// given
		DateEntity entity = addDateEntity();

		// when
		DateTime dateTimeUTC = entity.getDateTime().withZone(DateTimeZone.UTC);
		TypedQuery<DateEntity> findQuery = entityManager.createQuery(
				"select d from DateEntity d where d.dateTime = :dateTime", DateEntity.class);
		findQuery.setParameter("dateTime", dateTimeUTC);
		DateEntity foundEntity = findQuery.getSingleResult();

		// then
		assertNotNull(foundEntity);
		assertEquals(entity.getDate(), foundEntity.getDate());
		assertEquals(entity.getDateTime(), foundEntity.getDateTime());
	}

	private DateEntity addDateEntity() {
		DateTime dateTime = new DateTime();
		LocalDate date = dateTime.toLocalDate();

		DateEntity dateEntity = new DateEntity();
		dateEntity.setDate(date);
		dateEntity.setDateTime(dateTime);

		entityManager.persist(dateEntity);
		entityManager.flush();

		return dateEntity;
	}
}
