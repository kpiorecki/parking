package com.kpiorecki.parking.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.Parking;
import com.kpiorecki.parking.ejb.entity.Record;
import com.kpiorecki.parking.ejb.entity.User;
import com.kpiorecki.parking.ejb.entity.User_;

@Stateless
public class UserDao extends ArchivableDao<String, User> {

	@Inject
	private Logger logger;

	@Inject
	private ParkingDao parkingDao;

	public UserDao() {
		super(User.class);
	}

	@Override
	public void delete(String id) {
		super.delete(id);

		// remove user's parking assignments
		TypedQuery<Record> query = entityManager.createNamedQuery("Record.findRecordsByUser", Record.class);
		query.setParameter("login", id);
		query.setLockMode(LockModeType.OPTIMISTIC);
		List<Record> records = query.getResultList();

		for (Record record : records) {
			Parking parking = record.getParking();
			logger.info("deleting user={} record from parking={}", id, parking.getUuid());
			parking.removeRecord(record);

			parkingDao.save(parking);
		}
	}

	public boolean isLoginAvailable(String login) {
		TypedQuery<Long> query = entityManager.createNamedQuery("User.findLoginCount", Long.class);
		query.setParameter("login", login);
		Long result = query.getSingleResult();

		return result.longValue() == 0;
	}

	public void deleteOutdatedNotActivatedUsers() {
		logger.info("finding outdated not activated users to delete");
		DateTime dateTime = new DateTime();

		TypedQuery<String> query = entityManager.createNamedQuery("User.findOutdatedNotActivatedUsers", String.class);
		query.setParameter("dateTime", dateTime);

		List<String> userLogins = query.getResultList();
		if (userLogins.isEmpty()) {
			logger.info("did not find any not activated users to delete");
		} else {
			for (String login : userLogins) {
				logger.info("deleting not activated user={}", login);
				User user = entityManager.getReference(clazz, login);
				entityManager.remove(user);
			}
		}
	}

	@Override
	protected User findImpl(String id) {
		User user = super.findImpl(id);
		if (user != null && user.getActivationUuid() != null) {
			logger.info("user={} is not activated - returning null", id);
			return null;
		}
		return user;
	}

	@Override
	protected Predicate createFindQueryPredicate(CriteriaBuilder builder, CriteriaQuery<User> query, Root<User> root) {
		Predicate activatedPredicate = builder.isNull(root.get(User_.activationUuid));
		Predicate findQueryPredicate = super.createFindQueryPredicate(builder, query, root);

		return builder.and(findQueryPredicate, activatedPredicate);
	}
}
