package com.kpiorecki.parking.ejb.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.util.DomainException;

public abstract class GenericDao<K, E> {

	@Inject
	protected EntityManager entityManager;

	protected Class<E> clazz;

	@Inject
	private Logger logger;

	public GenericDao(Class<E> clazz) {
		this.clazz = clazz;
	}

	public E load(K id) {
		logger.info("loading {} entity with id={}", clazz.getSimpleName(), id);
		E entity = entityManager.find(clazz, id, LockModeType.OPTIMISTIC);
		if (entity == null) {
			String warnMessage = String.format("%s entity with id=%s not found", clazz.getSimpleName(), id);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}

		return entity;
	}

	public <V> E load(SingularAttribute<E, V> attribute, V value) {
		logger.info("finding {} single entity by {}={}", clazz.getSimpleName(), attribute.getName(), value);

		TypedQuery<E> findQuery = createFindQuery(attribute, value);
		try {
			return findQuery.getSingleResult();
		} catch (NoResultException | NonUniqueResultException e) {
			String warnMessage = String.format("did not find %s single entity by %s=%s", clazz.getSimpleName(),
					attribute.getName(), value);
			logger.warn(warnMessage);
			throw new DomainException(warnMessage);
		}
	}

	public <V> List<E> find(SingularAttribute<E, V> attribute, V value) {
		logger.info("finding {} entities by {}={}", clazz.getSimpleName(), attribute.getName(), value);

		TypedQuery<E> findQuery = createFindQuery(attribute, value);
		return findQuery.getResultList();
	}

	public List<E> findAll() {
		String message = String.format("finding all %s entities", clazz.getSimpleName());
		logger.info(message);

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = builder.createQuery(clazz);
		Root<E> root = query.from(clazz);

		query.select(root);
		adjustFindQuery(builder, query, root);

		TypedQuery<E> findAllQuery = entityManager.createQuery(query);
		findAllQuery.setLockMode(LockModeType.OPTIMISTIC);
		List<E> entities = findAllQuery.getResultList();

		logger.info("{} - {} found", message, entities.size());

		return entities;
	}

	public void save(E entity) {
		logger.info("saving {} entity", clazz.getSimpleName());
		if (entityManager.contains(entity)) {
			entityManager.lock(entity, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		} else {
			entityManager.persist(entity);
		}
	}

	public void delete(K id) {
		logger.info("deleting {} entity with id={}", clazz.getSimpleName(), id);

		E entityReference = entityManager.getReference(clazz, id);
		entityManager.remove(entityReference);
	}

	protected <V> TypedQuery<E> createFindQuery(SingularAttribute<E, V> attribute, V value) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> query = builder.createQuery(clazz);
		Root<E> root = query.from(clazz);

		query.select(root).where(builder.equal(root.get(attribute), value));
		adjustFindQuery(builder, query, root);

		TypedQuery<E> findQuery = entityManager.createQuery(query);
		findQuery.setLockMode(LockModeType.OPTIMISTIC);

		return findQuery;
	}

	protected void adjustFindQuery(CriteriaBuilder builder, CriteriaQuery<E> query, Root<E> root) {
		// may be overridden by subclasses
	}
}