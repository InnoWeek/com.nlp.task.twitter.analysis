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
import java.util.Locale;
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
            + "where T.sentiment != :sentiment "
            + "and (T.dateOfIndexing is null or T.dateOfIndexing < :reindexDate)";
    private static final String FIELD_TWEET_ID = "id";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_SENTIMENT = "sentiment";
    private static final String FIELD_TOPIC = "topic";

    private final EntityManagerFactory emf;
    private final Directory indexDirectory;

    public BackgroundIndexingJob(EntityManagerFactory entityManagerFactory, Directory indexDirectory) {
        this.emf = Objects.requireNonNull(entityManagerFactory);
        this.indexDirectory = Objects.requireNonNull(indexDirectory);
    }

    @Override
    public void run() {
        logger.log(Level.FINEST, "Background reindexing started.");
        EntityManager entityManager = null;
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new StandardAnalyzer());
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        try (IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)) {
            entityManager = emf.createEntityManager();
            index(indexWriter, entityManager);
            indexWriter.commit();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "The indexing job failed.", ex);
        } finally {
            if (null != entityManager) {
                entityManager.close();
            }
            logger.log(Level.FINEST, "Background reindexing completed.");
        }
    }

    private void index(IndexWriter indexWriter, EntityManager entityManager) throws IOException {
        final TypedQuery<Tweet> query = entityManager.createQuery(JPQL_QUERY_TWEETS_TO_INDEX, Tweet.class);
        query.setMaxResults(MAX_RESULTS);
        query.setParameter("sentiment", Tweet.Sentiment.UNKNOWN);
        query.setParameter("reindexDate", DEFAULT_REINDEX_DATE, TemporalType.TIMESTAMP);

        for (List<Tweet> tweets = query.getResultList(); !(tweets.isEmpty() || Thread.interrupted()); tweets = query.getResultList()) {
            for (Tweet tweet : tweets) {
                index(indexWriter, tweet);
                updateIndexingDate(entityManager, tweet);
            }
        }
    }

    private void index(IndexWriter indexWriter, Tweet tweet) throws IOException {
        final Document document = new Document();

        final Field tweetIdField = new StringField(FIELD_TWEET_ID, tweet.getId(), Field.Store.NO);
        document.add(tweetIdField);

        final Field contentField = new TextField(FIELD_CONTENT, tweet.getContent(), Field.Store.NO);
        document.add(contentField);

        if (tweet.getTopic() != null) {
            final String topic = tweet.getTopic().toLowerCase(Locale.ENGLISH);
            final Field topicField = new StringField(FIELD_TOPIC, topic, Field.Store.NO);
            document.add(topicField);
        }

        final Field sentimentField = new StringField(FIELD_SENTIMENT, tweet.getSentiment().name(), Field.Store.NO);
        document.add(sentimentField);

        final Term documentId = new Term(FIELD_TWEET_ID, tweet.getId());
        indexWriter.updateDocument(documentId, document);
    }

    private void updateIndexingDate(EntityManager entityManager, Tweet tweet) {
        entityManager.getTransaction().begin();
        tweet.setDateOfIndexing(Calendar.getInstance());
        entityManager.merge(tweet);
        entityManager.getTransaction().commit();
    }

}
