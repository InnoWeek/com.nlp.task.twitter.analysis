package com.nlp.task.twitter.analysis.rest;

import com.nlp.task.twitter.analysis.model.entity.Tweet;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.*;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Path("statistics")
public final class LuceneIndexEndpoint {
    private static final String TOPIC = "topic";
    private static final String POSITIVE = "positive";
    private static final String NEGATIVE = "negative";
    private static final String SENTIMENT = "sentiment";
    private static final Term TERM_SENTIMENT_POSITIVE = new Term(SENTIMENT, Tweet.Sentiment.POSITIVE.name());
    private static final Term TERM_SENTIMENT_NEGATIVE = new Term(SENTIMENT, Tweet.Sentiment.NEGATIVE.name());
    private static final TermsQuery QUERY_SENTIMENT_NEGATIVE = new TermsQuery(TERM_SENTIMENT_NEGATIVE);
    private static final TermsQuery QUERY_SENTIMENT_POSITIVE = new TermsQuery(TERM_SENTIMENT_POSITIVE);
    private static final String REGEX_WHITESPACE = "\\s+";
    @Context
    ServletContext servletContext;

    @GET
    @Path("byTopic")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByKeyWord(@QueryParam(TOPIC) String topic) throws IOException {
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
    public Response findByPhrase(@QueryParam("phrase") String phrase) throws IOException {
        final SearcherManager searchManager = (SearcherManager) servletContext.getAttribute(SearcherManager.class.getName());
        final IndexSearcher indexSearcher = searchManager.acquire();
        try {
            final PhraseQuery phraseQuery = new PhraseQuery("content", phrase.toLowerCase().trim().split(REGEX_WHITESPACE));
            final BooleanQuery queryPositive = new BooleanQuery.Builder()
                    .add(QUERY_SENTIMENT_POSITIVE, BooleanClause.Occur.MUST)
                    .add(phraseQuery, BooleanClause.Occur.MUST)
                    .build();
            final int numberOfPositiveTweets = indexSearcher.count(queryPositive);

            final BooleanQuery queryNegative = new BooleanQuery.Builder()
                    .add(QUERY_SENTIMENT_NEGATIVE, BooleanClause.Occur.MUST)
                    .add(phraseQuery, BooleanClause.Occur.MUST)
                    .build();
            final int numberOfNegativeTweets = indexSearcher.count(queryNegative);

            final Map<String, ? super Object> result = new HashMap<>();
            result.put("positive", numberOfPositiveTweets);
            result.put("negative", numberOfNegativeTweets);
            result.put("phrase", phrase);
            return Response.ok(result).build();
        } finally {
            searchManager.release(indexSearcher);
        }
    }
}
