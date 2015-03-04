package com.kpiorecki.parking.core.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;

@Stateless
public class GenericDao {

	@Inject
	private EntityManager entityManager;

	@Inject
	private Logger logger;

	public <E, V> E findEntityByUniqueField(SingularAttribute<E, V> attribute, V value) {
		Class<E> clazz = attribute.getDeclaringType().getJavaType();
		String message = String.format("finding %s.class entity by %s=%s", clazz.getSimpleName(), attribute.getName(), value);
		logger.info(message);

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = builder.createQuery(clazz);
		Root<E> entity = criteriaQuery.from(clazz);
		criteriaQuery.select(entity).where(builder.equal(entity.get(attribute), value));
		List<E> results = entityManager.createQuery(criteriaQuery).getResultList();

		if (results.size() == 1) {
			E result = results.get(0);
			logger.info("{} - found", message);
			return result;
		} else {
			logger.info("{} - not found", message);
			return null;
		}
	}
}
