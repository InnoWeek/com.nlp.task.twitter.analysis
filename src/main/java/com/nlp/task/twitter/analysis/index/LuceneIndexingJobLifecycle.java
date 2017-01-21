package com.nlp.task.twitter.analysis.index;

import com.nlp.task.twitter.analysis.config.persistency.EntityManagerFactoryProvider;

import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet listener that starts and stops the background indexer
 * on application start/stop
 */
@WebListener
public final class LuceneIndexingJobLifecycle implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(LuceneIndexingJobLifecycle.class.getName());
    private static final String INDEXING_THREAD_NAME_PREFIX = "background-indexer-";
    private static final int INITIAL_DELAY_SECONDS = 30;
    private static final int PERIOD_SECONDS = 15;
    private static final int COMPLETION_TIMEOUT_MILLIS = 250;

    private final AtomicInteger threadCounter = new AtomicInteger();
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread thread = defaultThreadFactory.newThread(r);
            thread.setName(INDEXING_THREAD_NAME_PREFIX + threadCounter.getAndIncrement());
            return thread;
        });

        final EntityManagerFactory entityManagerFactory = EntityManagerFactoryProvider.getEntityManagerFactory();
        final BackgroundIndexingJob backgroundIndexingJob = new BackgroundIndexingJob(entityManagerFactory);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                backgroundIndexingJob.run();
            } catch (RuntimeException ex) {
                logger.log(Level.SEVERE, "The background indexing job failed with an exception.", ex);
            }
        }, INITIAL_DELAY_SECONDS, PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (null != scheduledExecutorService) {
            try {
                scheduledExecutorService.shutdown();
                scheduledExecutorService.awaitTermination(COMPLETION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                logger.log(Level.SEVERE, "The background indexing job failed to shut down on time.", ex);
            }
        }
    }
}
