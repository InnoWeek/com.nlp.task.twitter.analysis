package com.nlp.task.twitter.analysis.config.persistency;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * This listener is responsible for teh initialization and destruction of the
 * entity manager factory. It must be initialized before any other listeners
 * that may try to use the entity manager factory.
 */
public final class EntityManagerFactoryProvider implements ServletContextListener {
    private static final String PERSISTENCE_UNIT_NAME = "com.nlp.task.twitter.analysis";
    private static EntityManagerFactory entityManagerFactory;

    @Resource
    DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final Map<Object, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, dataSource);
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);

        final ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(EntityManagerFactory.class.getName(), entityManagerFactory);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (null != entityManagerFactory) {
            entityManagerFactory.close();
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (null == entityManagerFactory) {
            throw new IllegalStateException("The entity manager factory has not been initialized.");
        }

        if (!entityManagerFactory.isOpen()) {
            throw new IllegalStateException("The entity manager factory has already been closed.");
        }

        return entityManagerFactory;
    }
}
