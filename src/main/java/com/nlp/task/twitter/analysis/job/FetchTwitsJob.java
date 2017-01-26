package com.nlp.task.twitter.analysis.job;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nlp.task.twitter.analysis.dao.TweetDao;
import com.nlp.task.twitter.analysis.model.entity.Tweet;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Job used to retrieve events from Ariba and post them to another instance.
 *
 */
public class FetchTwitsJob implements Job {

	private static List<String> topics = Arrays.asList("ronaldo", "kawasaki");

	private static final Logger logger = LoggerFactory.getLogger(FetchTwitsJob.class);

	private static TweetDao tweetDao = new TweetDao();

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		try {
			processTwits();
		} catch (Exception e) {
			logger.error("Error while fetching twits: ", e);
		}

	}

	private void processTwits() throws TwitterException, IOException {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey("<>")
		.setOAuthConsumerSecret("<>")
		.setOAuthAccessToken("<>")
		.setOAuthAccessTokenSecret("<>");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();

		for (String topic : topics) {
			Query q = new Query(topic).count(100).lang("en");
			List<Status> status = twitter.search(q).getTweets();
			for (Status st : status) {
				String tweetId = String.valueOf(st.getId());
				if (tweetDao.read(tweetId) == null) {
					Tweet tweet = new Tweet();
					tweet.setContent(st.getText());
					tweet.setId(tweetId);
					tweet.setTopic(topic);

					logger.debug(st.getText());
					tweetDao.create(tweet);
				}
			}
		}

	}

}