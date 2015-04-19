package com.kpiorecki.parking.ejb.dao;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.ArchivableEntity;
import com.kpiorecki.parking.ejb.entity.ArchivableEntity_;
import com.kpiorecki.parking.ejb.util.DomainException;

public class ArchivableDao<K, E extends ArchivableEntity> extends GenericDao<K, E> {

	@Inject
	private Logger logger;

	public ArchivableDao(Class<E> clazz) {
		super(clazz);
	}

	@Override
	public E load(K id) {
		E entity = super.load(id);

		if (entity.getRemoved()) {
			String warnMessage = String.format("%s entity with id=%s is removed", clazz.getSimpleName(), id);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}

		return entity;
	}

	@Override
	public void delete(K id) {
		logger.info("marking {} entity with id={} as removed", clazz.getSimpleName(), id);

		E entity = load(id);
		entity.setRemoved(true);
		save(entity);
	}

	@Override
	protected void adjustFindQuery(CriteriaBuilder builder, CriteriaQuery<E> query, Root<E> root) {
		Predicate notRemovedPredicate = builder.equal(root.get(ArchivableEntity_.removed), Boolean.FALSE);
		Predicate restriction = query.getRestriction();
		if (restriction != null) {
			query.where(restriction, notRemovedPredicate);
		} else {
			query.where(notRemovedPredicate);
		}
	}
}
