package com.kpiorecki.parking.core;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Resources {

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

}
