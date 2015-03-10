package com.kpiorecki.parking.core.util;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;

import com.kpiorecki.parking.core.exception.DomainException;

@Stateless
public class GenericDao {

	@Inject
	private EntityManager entityManager;

	@Inject
	private Logger logger;

	public <E, V> E findEntity(SingularAttribute<E, V> attribute, V value) {
		Class<E> clazz = attribute.getDeclaringType().getJavaType();
		String message = String.format("finding %s.class entity by %s=%s", clazz.getSimpleName(), attribute.getName(),
				value);
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

	public <E, V> E findExistingEntity(SingularAttribute<E, V> attribute, V value) {
		E entity = findEntity(attribute, value);
		if (entity == null) {
			Class<E> clazz = attribute.getDeclaringType().getJavaType();
			String message = String.format("%s.class entity with %s=%s was not found", clazz.getSimpleName(),
					attribute.getName(), value);
			logger.warn(message);
			throw new DomainException(message);
		}
		return entity;
	}

	public <E> List<E> findAllEntities(Class<E> clazz) {
		String message = String.format("finding all %s.class entities", clazz.getSimpleName());
		logger.info(message);

		CriteriaQuery<E> query = entityManager.getCriteriaBuilder().createQuery(clazz);
		TypedQuery<E> selectAllQuery = entityManager.createQuery(query.select(query.from(clazz)));
		List<E> entities = selectAllQuery.getResultList();

		logger.info("{} - {} found", message, entities.size());

		return entities;
	}
}
