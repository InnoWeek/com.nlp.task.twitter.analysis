package com.nlp.task.twitter.analysis.index;

import com.nlp.task.twitter.analysis.model.entity.Tweet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public final class BackgroundIndexingJob implements Runnable {
    private static final Logger logger = Logger.getLogger(BackgroundIndexingJob.class.getName());
    private static final Calendar DEFAULT_REINDEX_DATE = new Calendar.Builder().setInstant(0).build();
    private static final int MAX_RESULTS = 10;
    private static final String JPQL_QUERY_TWEETS_TO_INDEX = "select T from Tweet T "
            + "where (T.dateOfIndexing is null and T.sentiment <> UNKNOWN) "
            + "or T.dateOfIndexing < :reindexDate";
    private static final String FIELD_TWEET_ID = "id";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_SENTIMENT = "sentiment";

    private final EntityManagerFactory emf;
    private final Directory indexDirectory;

    public BackgroundIndexingJob(EntityManagerFactory entityManagerFactory, Directory indexDirectory) {
        this.emf = Objects.requireNonNull(entityManagerFactory);
        this.indexDirectory = Objects.requireNonNull(indexDirectory);
    }

    @Override
    public void run() {
        EntityManager entityManager = null;
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        try (IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {
            entityManager = emf.createEntityManager();
            index(indexWriter, entityManager);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "The indexing job failed.", ex);
        } finally {
            if (null != entityManager) {
                entityManager.close();
            }
        }
    }

    private void index(IndexWriter indexWriter, EntityManager entityManager) throws IOException {
        final TypedQuery<Tweet> query = entityManager.createQuery(JPQL_QUERY_TWEETS_TO_INDEX, Tweet.class);
        query.setMaxResults(MAX_RESULTS);
        query.setParameter("reindexDate", DEFAULT_REINDEX_DATE, TemporalType.TIMESTAMP);

        for (List<Tweet> tweets = query.getResultList(); !(tweets.isEmpty() || Thread.interrupted()); tweets = query.getResultList()) {
            index(indexWriter, tweets);
        }
    }

    private void index(IndexWriter indexWriter, List<Tweet> tweets) throws IOException {
        for (Tweet tweet : tweets) {
            index(indexWriter, tweet);
        }
    }

    private void index(IndexWriter indexWriter, Tweet tweet) throws IOException {
        final Document document = new Document();

        final String tweetId = Long.toString(tweet.getId());
        final Field tweetIdField = new StringField(FIELD_TWEET_ID, tweetId, Field.Store.NO);
        document.add(tweetIdField);

        final Field contentField = new TextField(FIELD_CONTENT, tweet.getContent(), Field.Store.NO);
        document.add(contentField);

        final Field sentimentField = new StringField(FIELD_SENTIMENT, tweet.getSentiment().name(), Field.Store.NO);
        document.add(sentimentField);

        final Term documentId = new Term(FIELD_TWEET_ID, tweetId);
        indexWriter.updateDocument(documentId, document);
    }

}
