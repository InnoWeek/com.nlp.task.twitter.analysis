package com.nlp.task.twitter.analysis.config.persistency;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity Manager Provider
 */
public class EntityManagerProvider {

	private static final String DEBUG_INITIALIZING_ENTITY_MANAGER = "Initializing Entity Manager ...";
	private static final String DEBUG_ENTITY_MANAGER_INITIALIZED = "Entity Manager Initialized.";

	private static final ThreadLocal<EntityManager> ENTITY_MANAGER = new ThreadLocal<>();

	private final static Logger logger = LoggerFactory.getLogger(EntityManagerProvider.class);

	/**
	 * Returns the entity manager for the current thread
	 *
	 * @return entity manager for the current thread
	 */
	public static EntityManager getEntityManager() {
		if (ENTITY_MANAGER.get() == null || !ENTITY_MANAGER.get().isOpen()) {
			initEntityManager();
		}
		return ENTITY_MANAGER.get();
	}

	/**
	 * Closes the entity manager for the current thread
	 */
	public void close() {
		EntityManager entityManager = ENTITY_MANAGER.get();
		ENTITY_MANAGER.set(null);
		if (entityManager != null) {
			entityManager.close();
		}
		ENTITY_MANAGER.remove();
	}

	private static void initEntityManager() {
		logger.debug(DEBUG_INITIALIZING_ENTITY_MANAGER);

		EntityManager entityManager = EntityManagerFactoryProvider.getEntityManagerFactory().createEntityManager();
		ENTITY_MANAGER.set(entityManager);

		logger.debug(DEBUG_ENTITY_MANAGER_INITIALIZED);
	}
}
