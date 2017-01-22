package com.nlp.task.twitter.analysis.model.entity;

import javax.persistence.*;
import java.util.Calendar;

/**
 *
 */
@Entity
public class Tweet {
    public enum Sentiment {
        POSITIVE,
        NEGATIVE,
        UNKNOWN
    }

    public Tweet() {
        sentiment = Sentiment.UNKNOWN;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    private String content;

    @Basic
    private String topic;

    @Basic
    @Column(unique = true)
    private String tweetId;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateOfAnalysis;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateOfIndexing;

    @Enumerated(EnumType.STRING)
    private Tweet.Sentiment sentiment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Calendar getDateOfAnalysis() {
        return dateOfAnalysis;
    }

    public void setDateOfAnalysis(Calendar dateOfAnalysis) {
        this.dateOfAnalysis = dateOfAnalysis;
    }

    public Calendar getDateOfIndexing() {
        return dateOfIndexing;
    }

    public void setDateOfIndexing(Calendar dateOfIndexing) {
        this.dateOfIndexing = dateOfIndexing;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }
}
