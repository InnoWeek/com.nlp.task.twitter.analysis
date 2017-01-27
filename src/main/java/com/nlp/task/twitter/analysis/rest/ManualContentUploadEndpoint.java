package com.nlp.task.twitter.analysis.rest;

import com.nlp.task.twitter.analysis.model.entity.Tweet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Path("content")
public final class ManualContentUploadEndpoint {
    private static final String PROPERTY_STATUS = "status";
    private static final String PROPERTY_ERRORS = "errors";

    @Context
    ServletContext servletContext;

    @POST
    @Path("tweet")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveTweet(Tweet tweet) {
        final Map<String, Object> response = new HashMap<>();
        final List<String> errors = validateTweet(tweet);

        if (!errors.isEmpty()) {
            response.put(PROPERTY_ERRORS, errors);
            response.put(PROPERTY_STATUS, "Malformed tweet content.");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        final EntityManagerFactory emf = (EntityManagerFactory) servletContext.getAttribute(EntityManagerFactory.class.getName());
        EntityManager entityManager = emf.createEntityManager();
        try {
            if (null == entityManager.find(Tweet.class, tweet.getId())) {
                entityManager.getTransaction().begin();
                entityManager.persist(tweet);
                entityManager.getTransaction().commit();
            }
        } finally {
            entityManager.close();
        }

        response.put(PROPERTY_STATUS, "ok");
        return Response.ok(response).build();
    }

    @POST
    @Path("tweets")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveTweets(List<Tweet> tweets) {
        final Map<String, Object> response = new HashMap<>();

        int index = 0;
        final List<Object> allErrors = new LinkedList<>();
        for (Tweet tweet : tweets) {
            final List<String> tweetErrors = validateTweet(tweet);
            if (!tweetErrors.isEmpty()) {
                final HashMap<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("tweet-index", index);
                errorDetails.put(PROPERTY_ERRORS, tweetErrors);
                allErrors.add(errorDetails);
            }
            index++;
        }

        if (!allErrors.isEmpty()) {
            response.put(PROPERTY_ERRORS, allErrors);
            response.put(PROPERTY_STATUS, "The tweets contain errors.");
            return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
        }

        final EntityManagerFactory emf = (EntityManagerFactory) servletContext.getAttribute(EntityManagerFactory.class.getName());
        EntityManager entityManager = emf.createEntityManager();
        try {
            entityManager.getTransaction().begin();
            for (Tweet tweet : tweets) {
                if (null == entityManager.find(Tweet.class, tweet.getId())) {
                    entityManager.persist(tweet);
                }
            }
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }

        response.put(PROPERTY_STATUS, "ok");
        return Response.ok(response).build();
    }

    private List<String> validateTweet(Tweet tweet) {
        final List<String> errors = new LinkedList<>();

        if (tweet.getContent() == null || tweet.getContent().isEmpty()) {
            errors.add("The tweet has no content.");
        }

        if (tweet.getId() == null || tweet.getId().isEmpty()) {
            errors.add("The tweet id is missing.");
        }

        if (tweet.getSentiment() != null && Tweet.Sentiment.UNKNOWN != tweet.getSentiment()) {
            errors.add("The incoming tweet cannot contain pre-determined sentiment.");
        }

        if (tweet.getDateOfAnalysis() != null) {
            errors.add("The incoming tweet cannot contain date of analysis");
        }

        if (tweet.getDateOfIndexing() != null) {
            errors.add("The incoming tweet cannot contain date of indexing");
        }
        return errors;
    }
}
