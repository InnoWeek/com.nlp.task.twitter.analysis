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
        NEGATIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateOfAnalysis;

    @Enumerated
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

    public Calendar getDateOfAnalysis() {
        return dateOfAnalysis;
    }

    public void setDateOfAnalysis(Calendar dateOfAnalysis) {
        this.dateOfAnalysis = dateOfAnalysis;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }

    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }
}
