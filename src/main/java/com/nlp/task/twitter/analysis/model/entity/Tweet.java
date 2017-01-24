package com.nlp.task.twitter.analysis.model.entity;

import javax.persistence.*;
import java.util.Calendar;

/**
 *
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "UnknownTweets", query = "SELECT a FROM Tweet a WHERE a.sentiment = com.nlp.task.twitter.analysis.model.entity.Tweet.Sentiment.UNKNOWN") })
public class Tweet {
	public enum Sentiment {
		POSITIVE, NEGATIVE, UNKNOWN
	}

	public Tweet() {
		sentiment = Sentiment.UNKNOWN;
	}

	@Id
	private String id;

	@Basic
	private String content;

	@Basic
	private String topic;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateOfAnalysis;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateOfIndexing;

	@Enumerated(EnumType.STRING)
	private Tweet.Sentiment sentiment;

	@PreUpdate
	@PrePersist
	void prePersist() {
		if (null == sentiment) {
			sentiment = Sentiment.UNKNOWN;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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
