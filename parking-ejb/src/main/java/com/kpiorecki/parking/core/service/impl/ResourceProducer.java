package com.kpiorecki.parking.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

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
}
