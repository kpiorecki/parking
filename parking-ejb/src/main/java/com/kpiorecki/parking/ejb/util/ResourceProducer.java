package com.kpiorecki.parking.ejb.util;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

	@PersistenceContext
	@Produces
	EntityManager entityManager;

	@Produces
	@Singleton
	Mapper createMapper() {
		List<String> mappingFiles = new ArrayList<String>();
		mappingFiles.add("dozer-mapping.xml");

		DozerBeanMapper mapper = new DozerBeanMapper();
		mapper.setMappingFiles(mappingFiles);

		return mapper;
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
