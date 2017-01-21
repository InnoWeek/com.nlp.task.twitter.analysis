package com.nlp.task.twitter.analysis.index;

import org.apache.lucene.store.Directory;

import javax.persistence.EntityManagerFactory;
import java.util.Objects;

/**
 *
 */
public final class BackgroundIndexingJob implements Runnable {
    private final EntityManagerFactory emf;

    public BackgroundIndexingJob(EntityManagerFactory entityManagerFactory, Directory indexDirectory) {
        this.emf = Objects.requireNonNull(entityManagerFactory);
    }

    @Override
    public void run() {

    }
}
