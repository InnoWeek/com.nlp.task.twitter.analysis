package com.nlp.task.twitter.analysis.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import com.nlp.task.twitter.analysis.config.persistency.BaseDao;
import com.nlp.task.twitter.analysis.model.entity.Tweet;

public class TweetDao extends BaseDao<Tweet> {

	public TweetDao() {
		super(Tweet.class);
	}
	
	public List<Tweet> findAllUnknown() {

		TypedQuery<Tweet> q = createNamedQuery("UnknownTweets");
		List<Tweet> resultList = q.getResultList();


		return resultList;
	}

}
