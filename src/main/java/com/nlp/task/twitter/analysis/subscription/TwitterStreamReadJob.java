package com.nlp.task.twitter.analysis.subscription;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang3.StringUtils;

import com.nlp.task.twitter.analysis.config.persistency.BaseDao;
import com.nlp.task.twitter.analysis.model.entity.Tweet;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@WebListener
public class TwitterStreamReadJob implements ServletContextListener{
	
	private TwitterStream twitterStream = null;
	
	private BaseDao<Tweet> tweetDao;
	
	private StatusListener listener = new StatusListener(){

		private final Logger LOGGER = Logger.getLogger(StatusListener.class.getName());
		
		private static final long THREAD_SLEEP_TIME = 3000;
		
		@Override
		public void onException(Exception exception) {
			LOGGER.log(Level.SEVERE, "Error reading tweets from stream.", exception);
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice deletion) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStallWarning(StallWarning arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Called on receiving a tweet from the stream. Creates a tweet object with the tweet id and content
		 * then saves it in the database.
		 * @param status the tweet
		 */
		@Override
		public void onStatus(Status status) {
			String tweetId = String.valueOf(status.getId());
			String tweetText = status.getText();
			if (StringUtils.isNoneBlank(tweetId, tweetText)) {
				Tweet tweet = new Tweet();
				tweet.setId(tweetId);
				tweet.setContent(tweetText);
				saveTweet(tweet);
			}
			
			try {
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				LOGGER.log(Level.SEVERE, null, e);
			}
		}
		
		private void saveTweet(Tweet tweet) {
			if (tweetDao.read(tweet.getId()) == null) {
				tweetDao.create(tweet);
			} else {
				tweetDao.update(tweet);
			}
		}

		@Override
		public void onTrackLimitationNotice(int arg0) {
			// TODO Auto-generated method stub
			
		}
        
    };
	
    /**
     * Initializes the twitter stream to read tweets in english language.
     */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		tweetDao = new BaseDao<>(Tweet.class);
	    
		twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);
	    twitterStream.sample("en");
	}
	
	/**
	 * Shuts down the twitter stream
	 */
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (twitterStream != null) {
			twitterStream.clearListeners();
			twitterStream.cleanUp();
			twitterStream.shutdown();
		}
	}

}
