package com.nlp.task.twitter.analysis.job;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context listener for receiving notification events about ServletContext
 * lifecycle changes.
 *
 */
public class TwitterJobsContextListener implements ServletContextListener {

	private static final String ERROR_WHILE_SCHEDULER_HANDLER_SHUTTING_DOWN_MESSAGE = "Error occurred while shutting down scheduler: {}";
	private static final String ERROR_WHILE_SCHEDULER_HANDLER_INITIALIZING_MESSAGE = "Error occurred while initializing scheduler: {}";

	private static final Logger logger = LoggerFactory.getLogger(TwitterJobsContextListener.class);

	public TwitterJobsContextListener() {
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		try {
			SchedulerHandler handler = SchedulerHandler.getInstance();
			handler.startAndSchedule();
		} catch (SchedulerException e) {
			logger.error(ERROR_WHILE_SCHEDULER_HANDLER_INITIALIZING_MESSAGE, e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {

		try {
			SchedulerHandler handler = SchedulerHandler.getInstance();
			handler.stop();
		} catch (SchedulerException e) {
			logger.error(ERROR_WHILE_SCHEDULER_HANDLER_SHUTTING_DOWN_MESSAGE, e.getMessage());
			throw new RuntimeException(e);
		}

	}

}