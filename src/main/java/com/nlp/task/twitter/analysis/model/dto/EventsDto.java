package com.nlp.task.twitter.analysis.model.dto;

import java.util.Arrays;

/**
 * Events DTO
 *
 */
public class EventsDto {

	private String siteId;
	private EventDto[] events;

	/**
	 * Returns siteId
	 *
	 * @return siteId
	 */
	public String getSiteId() {
		return siteId;
	}

	/**
	 * Sets siteId
	 * 
	 * @param siteId
	 */
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	/**
	 * Returns events
	 *
	 * @return events
	 */
	public EventDto[] getEvents() {
		return events;
	}

	/**
	 * Sets events
	 * 
	 * @param events
	 */
	public void setEvents(EventDto[] events) {
		this.events = events;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Events [siteId=" + siteId + ", events=" + Arrays.toString(events) + "]";
	}

}
