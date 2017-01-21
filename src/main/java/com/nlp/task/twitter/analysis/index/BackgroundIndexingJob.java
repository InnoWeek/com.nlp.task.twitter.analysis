package com.nlp.task.twitter.analysis.index;

import javax.persistence.EntityManagerFactory;
import java.util.Objects;

/**
 *
 */
public final class BackgroundIndexingJob implements Runnable {
    private final EntityManagerFactory emf;

    public BackgroundIndexingJob(EntityManagerFactory entityManagerFactory) {
        this.emf = Objects.requireNonNull(entityManagerFactory);
    }

    @Override
    public void run() {

    }
}
