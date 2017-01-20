package com.nlp.task.twitter.analysis.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nlp.task.twitter.analysis.config.persistency.BaseDao;
import com.nlp.task.twitter.analysis.model.entity.Event;

/**
 * Event DAO
 *
 */
public class EventDao extends BaseDao<Event> {

	private static final Logger logger = LoggerFactory.getLogger(EventDao.class);

	private static final String DEBUG_EVENT_FOUND_WITH_EVENT_ID_MESSAGE = "Event found with event ID: [{}]";
	private static final String DEBUG_EVENT_NOT_FOUND_WITH_EVENT_ID_MESSAGE = "Event not found with event ID: [{}]";
	private static final String DEBUG_FINDING_EVENT_WITH_EVENT_ID_MESSAGE = "Finding event with event ID: [{}]";

	public EventDao() {
		super(Event.class);
	}

	/**
	 * Finds event by event id
	 *
	 * @param eventId
	 *            - event id
	 * @return found event or null
	 */
	public Event findByEventId(String eventId) {
		logger.debug(DEBUG_FINDING_EVENT_WITH_EVENT_ID_MESSAGE, eventId);

		TypedQuery<Event> q = createNamedQuery(Event.QUERY_FIND_BY_EVENT_ID);
		q.setParameter(Event.QUERY_PARAM_FIND_BY_EVENT_ID, eventId);
		List<Event> resultList = q.getResultList();

		Event event = null;
		if (!resultList.isEmpty()) {
			event = resultList.get(0);
			logger.debug(DEBUG_EVENT_FOUND_WITH_EVENT_ID_MESSAGE, eventId);
		} else {
			logger.debug(DEBUG_EVENT_NOT_FOUND_WITH_EVENT_ID_MESSAGE, eventId);
		}

		return event;
	}

}
