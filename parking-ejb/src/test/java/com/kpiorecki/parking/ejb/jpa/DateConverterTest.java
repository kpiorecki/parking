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
import org.junit.Before;
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

	private DateTime dateTime;
	private LocalDate date;

	@Before
	public void prepareData() {
		dateTime = new DateTime();
		date = dateTime.toLocalDate();

		DateEntity dateEntity = new DateEntity();
		dateEntity.setDate(date);
		dateEntity.setDateTime(dateTime);

		entityManager.persist(dateEntity);
		entityManager.flush();
	}

	@Test
	public void shouldFindByLocalDate() {
		// given
		TypedQuery<DateEntity> findQuery = entityManager.createQuery("select d from DateEntity d where d.date = :date",
				DateEntity.class);
		findQuery.setParameter("date", date);

		// when
		DateEntity dateEntity = findQuery.getSingleResult();

		// then
		assertNotNull(dateEntity);
		assertEquals(date, dateEntity.getDate());
		assertEquals(dateTime, dateEntity.getDateTime());
	}

	@Test
	public void shouldFindByUTCDateTime() {
		// given
		DateTime dateTimeUTC = dateTime.withZone(DateTimeZone.UTC);
		TypedQuery<DateEntity> findQuery = entityManager.createQuery(
				"select d from DateEntity d where d.dateTime = :dateTime", DateEntity.class);
		findQuery.setParameter("dateTime", dateTimeUTC);

		// when
		DateEntity dateEntity = findQuery.getSingleResult();

		// then
		assertNotNull(dateEntity);
		assertEquals(date, dateEntity.getDate());
		assertEquals(dateTime, dateEntity.getDateTime());
	}
}
