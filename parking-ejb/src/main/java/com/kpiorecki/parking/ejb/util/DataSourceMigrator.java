package com.kpiorecki.parking.ejb.util;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;

@Singleton
@Startup
public class DataSourceMigrator {

	@Inject
	private Logger logger;

	@Inject
	private DataSource dataSource;

	@PostConstruct
	void migrate() {
		logger.info("migrating data source");

		Flyway flyway = new Flyway();
		flyway.setBaselineOnMigrate(true);
		flyway.setDataSource(dataSource);
		flyway.migrate();
	}
}