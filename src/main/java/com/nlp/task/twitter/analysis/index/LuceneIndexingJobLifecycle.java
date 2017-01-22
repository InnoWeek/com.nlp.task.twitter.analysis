package com.nlp.task.twitter.analysis.index;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public final class LuceneIndexingJobLifecycle implements ServletContextListener, IndexSearcherFactory {
    private static final Logger logger = Logger.getLogger(LuceneIndexingJobLifecycle.class.getName());
    private static final String INDEXING_THREAD_NAME_PREFIX = "background-indexer-";
    private static final int INITIAL_DELAY_SECONDS = 30;
    private static final int DELAY_BETWEEN_EXECUTIONS = 15;
    private static final int COMPLETION_TIMEOUT_MILLIS = 250;
    private static final int INDEX_READER_MAX_AGE = 90 * 1000;

    private final AtomicInteger threadCounter = new AtomicInteger();
    private ScheduledExecutorService scheduledExecutorService;
    private FSDirectory indexDirectory;

    @Resource(name = "lucene-index-directory")
    String indexDirectoryPath;
    private ShareableIndexSearcher indexSearcher;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            final EntityManagerFactory entityManagerFactory = acquireEntityManagerFactory(sce);
            final Directory directory = openTheIndexDirectory(sce);
            initializeTheScheduledExecutorService();
            scheduleTheIndexingJob(entityManagerFactory, directory);
            registerTheIndesSearcherFactory(sce);
        } catch (IOException ex) {
            throw new IllegalStateException("Some components failed to initialize properly.", ex);
        }
    }

    private EntityManagerFactory acquireEntityManagerFactory(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        final EntityManagerFactory entityManagerFactory = (EntityManagerFactory) servletContext.getAttribute(EntityManagerFactory.class.getName());
        if (null == entityManagerFactory) {
            throw new IllegalStateException("The entity manager factory has not been initialized.");
        }
        return entityManagerFactory;
    }

    private Directory openTheIndexDirectory(ServletContextEvent sce) throws IOException {
        final ServletContext servletContext = sce.getServletContext();
        final Path pathToIndexDirectory = Paths.get(indexDirectoryPath);
        indexDirectory = FSDirectory.open(pathToIndexDirectory);
        servletContext.setAttribute(Directory.class.getName(), indexDirectory);
        return indexDirectory;
    }

    private void initializeTheScheduledExecutorService() {
        final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
            final Thread thread = defaultThreadFactory.newThread(r);
            thread.setName(INDEXING_THREAD_NAME_PREFIX + threadCounter.getAndIncrement());
            return thread;
        });
    }

    private void scheduleTheIndexingJob(EntityManagerFactory entityManagerFactory, Directory directory) {
        final BackgroundIndexingJob backgroundIndexingJob = new BackgroundIndexingJob(entityManagerFactory, directory);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                backgroundIndexingJob.run();
            } catch (RuntimeException ex) {
                logger.log(Level.SEVERE, "The background indexing job failed with an exception.", ex);
            }
        }, INITIAL_DELAY_SECONDS, DELAY_BETWEEN_EXECUTIONS, TimeUnit.SECONDS);
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

            try {
                indexSearcher.close();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to close the index searcher.", ex);
            }

            try {
                indexDirectory.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to close the index directory.", ex);
            }
        }
    }

    private void registerTheIndesSearcherFactory(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute(IndexSearcherFactory.class.getName(), this);
    }

    @Override
    public synchronized ShareableIndexSearcher getIndexSearcher() throws IOException {
        if (null == indexSearcher || indexSearcher.isOutdated()) {
            if (null != indexSearcher) {
                indexSearcher.close();
            }
            final IndexReader indexReader = StandardDirectoryReader.open(indexDirectory);
            indexSearcher = new ShareableIndexSearcher(indexReader, INDEX_READER_MAX_AGE);
        }

        return indexSearcher.share();
    }
}
