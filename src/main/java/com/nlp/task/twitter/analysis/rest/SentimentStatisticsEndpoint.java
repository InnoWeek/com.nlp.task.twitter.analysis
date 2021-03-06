package com.nlp.task.twitter.analysis.rest;

import com.nlp.task.twitter.analysis.model.entity.Tweet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Path("statistics")
public final class SentimentStatisticsEndpoint {
    private static final String TOPIC = "topic";
    private static final String PHRASE = "phrase";
    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";
    private static final String SENTIMENT = "sentiment";
    private static final String CONTENT = "content";
    private static final Term TERM_SENTIMENT_POSITIVE = new Term(SENTIMENT, Tweet.Sentiment.POSITIVE.name());
    private static final Term TERM_SENTIMENT_NEGATIVE = new Term(SENTIMENT, Tweet.Sentiment.NEGATIVE.name());
    private static final TermsQuery QUERY_SENTIMENT_NEGATIVE = new TermsQuery(TERM_SENTIMENT_NEGATIVE);
    private static final TermsQuery QUERY_SENTIMENT_POSITIVE = new TermsQuery(TERM_SENTIMENT_POSITIVE);
    private static final String REGEX_WHITESPACE = "\\s+";
    private static final String FORMAT_KEY_VALUE = "\"%s\":\"%s\",";
    @Context
    ServletContext servletContext;

    @GET
    @Path("byTopic")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByTopic(@QueryParam(TOPIC) String topic) throws IOException {
        final SearcherManager searchManager = (SearcherManager) servletContext.getAttribute(SearcherManager.class.getName());
        final IndexSearcher indexSearcher = searchManager.acquire();
        try {
            final Term topicTerm = new Term(TOPIC, topic.toLowerCase());

            final BooleanQuery queryPositive = new BooleanQuery.Builder()
                    .add(new TermsQuery(topicTerm), BooleanClause.Occur.MUST)
                    .add(QUERY_SENTIMENT_POSITIVE, BooleanClause.Occur.MUST)
                    .build();
            final int numberOfPositiveTweets = indexSearcher.count(queryPositive);

            final BooleanQuery queryNegative = new BooleanQuery.Builder()
                    .add(new TermsQuery(topicTerm), BooleanClause.Occur.MUST)
                    .add(QUERY_SENTIMENT_NEGATIVE, BooleanClause.Occur.MUST)
                    .build();
            final int numberOfNegativeTweets = indexSearcher.count(queryNegative);

            final Map<String, ? super Object> result = new HashMap<>();
            result.put(POSITIVE, numberOfPositiveTweets);
            result.put(NEGATIVE, numberOfNegativeTweets);
            result.put(TOPIC, topic);
            return Response.ok(result).build();
        } finally {
            searchManager.release(indexSearcher);
        }
    }

    @GET
    @Path("byPhrase")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByPhrase(@QueryParam(PHRASE) String phrase) throws IOException {
        final SearcherManager searchManager = (SearcherManager) servletContext.getAttribute(SearcherManager.class.getName());
        final IndexSearcher indexSearcher = searchManager.acquire();
        try {
            final String[] words = phrase.toLowerCase().trim().split(REGEX_WHITESPACE);
            final BooleanQuery.Builder positiveQueryBuilder = new BooleanQuery.Builder()
                    .add(QUERY_SENTIMENT_POSITIVE, BooleanClause.Occur.MUST);
            for (String word : words) {
                final Term contentTerm = new Term(CONTENT, word);
                positiveQueryBuilder.add(new TermsQuery(contentTerm), BooleanClause.Occur.MUST);
            }
            final BooleanQuery queryPositive = positiveQueryBuilder.build();
            final int numberOfPositiveTweets = indexSearcher.count(queryPositive);


            final BooleanQuery.Builder negativeQueryBuilder = new BooleanQuery.Builder()
                    .add(QUERY_SENTIMENT_NEGATIVE, BooleanClause.Occur.MUST);
            for (String word : words) {
                final Term contentTerm = new Term(CONTENT, word);
                negativeQueryBuilder.add(new TermsQuery(contentTerm), BooleanClause.Occur.MUST);
            }
            final BooleanQuery queryNegative = negativeQueryBuilder.build();
            final int numberOfNegativeTweets = indexSearcher.count(queryNegative);

            final Map<String, ? super Object> result = new HashMap<>();
            result.put(POSITIVE, numberOfPositiveTweets);
            result.put(NEGATIVE, numberOfNegativeTweets);
            result.put(PHRASE, phrase);
            return Response.ok(result).build();
        } finally {
            searchManager.release(indexSearcher);
        }
    }

    @GET
    @Path("downloadByPhrase")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadByPhrase(@QueryParam(PHRASE) String phrase) throws IOException {
        final SearcherManager searchManager = (SearcherManager) servletContext.getAttribute(SearcherManager.class.getName());
        final IndexSearcher indexSearcher = searchManager.acquire();
        try {
            final String[] words = phrase.toLowerCase().trim().split(REGEX_WHITESPACE);
            final TweetCollector tweetCollector = new TweetCollector();
            final BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            for (String word : words) {
                final Term contentTerm = new Term(CONTENT, word);
                queryBuilder.add(new TermsQuery(contentTerm), BooleanClause.Occur.SHOULD);
            }
            final BooleanQuery query = queryBuilder.build();
            indexSearcher.search(query, tweetCollector);

            return Response.ok((StreamingOutput) output -> {
                EntityManager em = null;
                try {
                    final EntityManagerFactory emf = (EntityManagerFactory) servletContext.getAttribute(EntityManagerFactory.class.getName());
                    em = emf.createEntityManager();
                    final BitSet bitSet = tweetCollector.collect();
                    final PrintWriter writer = new PrintWriter(output);
                    writer.println("[");
                    for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
                        final Document doc = indexSearcher.doc(i);
                        final IndexableField idField = doc.getField("id");
                        if (null != idField) {
                            final String tweetId = idField.stringValue();
                            if (null != tweetId) {
                                final Tweet tweet = em.find(Tweet.class, tweetId);
                                if (null != tweet) {
                                    writer.println("{");
                                    writer.println(String.format(FORMAT_KEY_VALUE, "id", tweet.getId()));
                                    writer.println(String.format(FORMAT_KEY_VALUE, "content", tweet.getContent()));
                                    writer.println(String.format(FORMAT_KEY_VALUE, "sentiment", tweet.getSentiment().name()));
                                    writer.println("},");
                                }
                            }
                        }
                    }
                    writer.println("]");
                    writer.flush();
                } finally {
                    searchManager.release(indexSearcher);
                    if (null != em) {
                        em.close();
                    }
                }
            }).build();
        } catch (Exception ex) {
            searchManager.release(indexSearcher);
        }

        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
    }
}


class TweetCollector implements Collector, LeafCollector {
    final ArrayList<BitSet> bitSets = new ArrayList<>();
    BitSet bitSet;
    int offset = 0;

    @Override
    public void setScorer(Scorer scorer) throws IOException {
        //no-op
    }

    @Override
    public void collect(int doc) throws IOException {
        bitSet.set(offset + doc);
    }

    @Override
    public LeafCollector getLeafCollector(LeafReaderContext context) throws IOException {
        bitSet = new BitSet();
        offset = context.docBase;
        bitSets.add(bitSet);
        return this;
    }

    @Override
    public boolean needsScores() {
        return false;
    }

    public BitSet collect() {
        BitSet bitSet = new BitSet();
        for (BitSet set : bitSets) {
            bitSet.or(set);
        }
        return bitSet;
    }
}
