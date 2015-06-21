package com.kpiorecki.parking.ejb.dao;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.ArchivableEntity;
import com.kpiorecki.parking.ejb.entity.ArchivableEntity_;

public class ArchivableDao<K, E extends ArchivableEntity> extends GenericDao<K, E> {

	@Inject
	private Logger logger;

	public ArchivableDao(Class<E> clazz) {
		super(clazz);
	}

	@Override
	public void delete(K id) {
		logger.info("marking {} entity with id={} as removed", clazz.getSimpleName(), id);

		E entity = load(id);
		entity.setRemoved(true);
		save(entity);
	}

	@Override
	protected E findImpl(K id) {
		E entity = super.findImpl(id);
		if (entity != null && entity.getRemoved()) {
			logger.info("{} entity with id={} is removed - returning null", clazz.getSimpleName(), id);
			return null;
		}
		return entity;
	}

	@Override
	protected void adjustFindQuery(CriteriaBuilder builder, CriteriaQuery<E> query, Root<E> root) {
		Predicate predicate = createFindQueryPredicate(builder, query, root);
		Predicate restriction = query.getRestriction();
		if (restriction != null) {
			query.where(restriction, predicate);
		} else {
			query.where(predicate);
		}
	}

	protected Predicate createFindQueryPredicate(CriteriaBuilder builder, CriteriaQuery<E> query, Root<E> root) {
		return builder.isFalse(root.get(ArchivableEntity_.removed));
	}

}
