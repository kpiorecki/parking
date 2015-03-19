package com.kpiorecki.parking.ejb.dao;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.slf4j.Logger;

import com.kpiorecki.parking.ejb.entity.User;

@Stateless
public class UserDao extends ArchivableDao<String, User> {

	@Inject
	private Logger logger;

	public UserDao() {
		super(User.class);
	}

	@Override
	public void delete(String id) {
		super.delete(id);

		entityManager.flush();
		entityManager.clear();
		entityManager.getEntityManagerFactory().getCache().evictAll();

		Query deleteRecordsQuery = entityManager.createNamedQuery("Record.deleteUserRecords");
		deleteRecordsQuery.setParameter("login", id);
		int deletedRecords = deleteRecordsQuery.executeUpdate();

		logger.info("deleted {} record(s) from user={}", deletedRecords, id);
	}
}
