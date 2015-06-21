package com.kpiorecki.parking.ejb.util;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

@Singleton
public class ResourceProducer {

	@PersistenceContext(unitName = "parkingPU")
	@Produces
	EntityManager entityManager;

	@Produces
	@Resource(lookup = "jdbc/parking")
	DataSource dataSource;

	@Produces
	@Resource(lookup = "mail/session")
	Session mailSession;

	@Produces
	@Singleton
	Mapper createMapper() {
		return new DozerBeanMapper();
	}

	@Produces
	Logger createLogger(final InjectionPoint ip) {
		return LoggerFactory.getLogger(ip.getMember().getDeclaringClass());
	}

	@Produces
	@DateFormatter
	DateTimeFormatter createDateFormatter() {
		return ISODateTimeFormat.date();
	}

	@Produces
	@Singleton
	Configuration createTemplateConfiguration() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
		configuration.setClassForTemplateLoading(getClass(), "/templates");
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		return configuration;
	}

}
