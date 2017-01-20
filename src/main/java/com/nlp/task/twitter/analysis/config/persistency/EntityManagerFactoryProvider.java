package com.nlp.task.twitter.analysis.config.persistency;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity Manager Factory Provider.
 */
public class EntityManagerFactoryProvider {

	private static final String DEBUG_CLOSING_ENTITY_MANAGER_FACTORY = "Closing Entity Manager Factory ...";
	private static final String DEBUG_ENTITY_MANAGER_FACTORY_CLOSED = "Entity Manager Factory closed.";
	private static final String DEBUG_ENTITY_MANAGER_FACTORY_INITIALIZED = "Entity Manager Factory initialized.";
	private static final String DEBUG_INITIALIZING_ENTITY_MANAGER_FACTORY = "Initializing Entity Manager Factory ...";

	public static final String PERSISTENCE_UNIT_NAME = "com.nlp.task.twitter.analysis";

	private final static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

	private static EntityManagerFactory entityManagerFactory = null;

	/**
	 * Returns the entity manager factory.
	 *
	 * @return the entity manager factory
	 */
	public synchronized static EntityManagerFactory getEntityManagerFactory() {
		if (EntityManagerFactoryProvider.entityManagerFactory == null
				|| !EntityManagerFactoryProvider.entityManagerFactory.isOpen()) {
			initEntityManagerFactory(DataSourceProvider.getInstance().getDataSource());
		}
		return EntityManagerFactoryProvider.entityManagerFactory;
	}

	private static void initEntityManagerFactory(DataSource dataSource) {
		logger.debug(DEBUG_INITIALIZING_ENTITY_MANAGER_FACTORY);
		Map<Object, Object> properties = new HashMap<>();
		properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
		EntityManagerFactoryProvider.entityManagerFactory = Persistence
				.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
		logger.debug(DEBUG_ENTITY_MANAGER_FACTORY_INITIALIZED);
	}

	/**
	 * Closes the entity manager factory.
	 */
	public synchronized void close() {
		logger.debug(DEBUG_CLOSING_ENTITY_MANAGER_FACTORY);
		if (EntityManagerFactoryProvider.entityManagerFactory != null) {
			EntityManagerFactoryProvider.entityManagerFactory.close();
			EntityManagerFactoryProvider.entityManagerFactory = null;
		}
		logger.debug(DEBUG_ENTITY_MANAGER_FACTORY_CLOSED);
	}
}
