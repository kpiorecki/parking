package com.kpiorecki.parking.core.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.kpiorecki.parking.core.entity.UuidEntity;

public class UuidEntityDao {

	@Inject
	private EntityManager entityManager;

	@Inject
	private Logger logger;

	public <E extends UuidEntity> E findByUuid(Class<E> clazz, String uuid) {
		String message = String.format("finding %s entity by uuid=%s", clazz.getSimpleName(), uuid);
		logger.info(message);
		if (uuid == null) {
			logger.debug("{} - uuid is null - entity not found", message);
			return null;
		}

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = builder.createQuery(clazz);
		Root<E> entity = criteriaQuery.from(clazz);
		criteriaQuery.select(entity).where(builder.equal(entity.get("uuid"), uuid));
		List<E> results = entityManager.createQuery(criteriaQuery).getResultList();

		if (results.size() == 1) {
			E result = results.get(0);
			logger.debug("{} - found {}", message, result);
			return result;
		} else {
			logger.debug("{} - did not find unique entity", message);
			return null;
		}
	}
}
