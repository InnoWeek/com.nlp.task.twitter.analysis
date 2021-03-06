package com.nlp.task.twitter.analysis;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.nlp.task.twitter.analysis.rest.AvailabilityCheckEndpoint;
import com.nlp.task.twitter.analysis.rest.SentimentStatisticsEndpoint;
import com.nlp.task.twitter.analysis.rest.ManualContentUploadEndpoint;
import com.nlp.task.twitter.analysis.rest.TweetService;

/**
 * Initialize application and it's services.
 *
 */
public class ApplicationConfiguration extends Application {

	private static final String DEBUG_SERVICE_INITIALIZED = "Service: {} initialized.";

	private Set<Class<?>> services = new HashSet<>();

	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.ws.rs.core.Application#getClasses()
	 */
	@Override
	public Set<Class<?>> getClasses() {
		return getServices();
	}

	private Set<Class<?>> getServices() {
		services.add(AvailabilityCheckEndpoint.class);
		services.add(SentimentStatisticsEndpoint.class);
		services.add(ManualContentUploadEndpoint.class);
		services.add(TweetService.class);
		services.add(JacksonJaxbJsonProvider.class);
		logger.debug(DEBUG_SERVICE_INITIALIZED, JacksonJaxbJsonProvider.class.getName());
		return services;
	}
}
