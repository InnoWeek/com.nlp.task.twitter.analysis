package com.nlp.task.twitter.analysis.resource;

import java.text.MessageFormat;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nlp.task.twitter.analysis.dao.EventDao;
import com.nlp.task.twitter.analysis.model.dto.EventDto;
import com.nlp.task.twitter.analysis.model.entity.Event;

/**
 * Used to saves, retrieves and deletes events
 *
 */
@Path("events")
public class EventResource {

	private static final String EVENT_WITH_EVENT_ID_WAS_SUCCESSFULLY_DELETED = "Event with event id [{0}] was successfully deleted.";
	private static final String THE_EVENTS_WERE_SUCCESSFULLY_DELETED = "The events were successfully deleted.";
	private static final String EVENT_WITH_ID_IS_ADDED_IN_DATABASE = "Event with id [{0}] is added in database.";

	private static final String ERROR_EVENT_WITH_EVENT_ID_IS_NOT_DELETED_SUCCESSFULLY = "Event with event id [{0}] is not deleted successfully.";
	private static final String ERROR_EVENT_WITH_EVENT_ID_IS_NOT_FOUND = "Event with event id [{0}] is not found.";
	private static final String ERROR_EVENT_WITH_ID_ALREADY_EXISTS_IN_DATABASE = "Event with event id [{0}] already exists in database.";
	private static final String ERROR_PROBLEM_WITH_ADDING_EVENT_WITH_ID = "Problem with adding event with event id [{0}].";
	private static final String ERROR_DELETING_EVENT_WITH_ID_DATABASE = "Problem occured while deleting an event with event id [{0}].";
	private static final String ERROR_DELETING_ALL_EVENTS_DATABASE = "Problem occured while deleting all events.";
	private static final String ERROR_SAVING_EVENT_WITH_ID_DATABASE = "Problem occured while saving an event with event id [{0}] in database.";
	private static final String ERROR_EVENT_ID_NULL = "Event id should not be null.";
	private static final String ERROR_FETCHING_EVENTS_DATABASE = "Problem occured while fetching events.";

	private static final String DEBUG_FETCH_STARTING = "Started fetching events.";
	private static final String DEBUG_FETCH_DONE = "Done fetching events.";
	private static final String DEBUG_DONE_DELETING_EVENT_WITH_ID = "Done deleting event with event id [{}].";
	private static final String DEBUG_STARTED_DELETING_EVENT_WITH_ID = "Started deleting event with event id [{}].";
	private static final String DEBUG_DONE_DELETING_ALL_EVENTS = "Done deleting all events.";
	private static final String DEBUG_STARTED_DELETING_ALL_EVENTS = "Started deleting all events.";
	private static final String DEBUG_DONE_CREATING_NEW_EVENT_WITH_ID = "Done creating new event with event id [{}].";
	private static final String DEBUG_STARTED_CREATING_NEW_EVENT = "Started creating new event.";

	private EventDao eventDao = new EventDao();
	private static ModelMapper modelMapper = new ModelMapper();

	private static final Logger logger = LoggerFactory.getLogger(EventResource.class);

	static {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}

	/**
	 * Retrieve all events from database
	 *
	 * @return response - HTTP OK 200 for success with events list as JSON or
	 *         HTTP Error 500 Internal server error
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveEvents() {
		logger.debug(DEBUG_FETCH_STARTING);
		Response response = null;

		try {
			List<Event> events = eventDao.readAll();
			response = Response.status(Response.Status.OK).entity(convertToDtoList(events)).build();
		} catch (Exception e) {
			logger.error(ERROR_FETCHING_EVENTS_DATABASE, e);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ERROR_FETCHING_EVENTS_DATABASE)
					.build();
		}

		logger.debug(DEBUG_FETCH_DONE);
		return response;
	}

	/**
	 * Save an event in database
	 *
	 * @param event
	 *            - Event
	 * @return response - HTTP OK 200 for success with events as JSON, HTTP
	 *         Error 409 Conflicts for adding event which already exists, HTTP
	 *         Error 400 Bad request for invalid event id or HTTP Error 500
	 *         Internal server error
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveEvent(EventDto eventDto) {
		logger.debug(DEBUG_STARTED_CREATING_NEW_EVENT);
		Response response = null;

		try {
			if (eventDto.getEventId() != null) {
				Event existingEvent = eventDao.findByEventId(eventDto.getEventId());

				if (existingEvent == null) {
					Event event = convertToEntity(eventDto);

					if (eventDao.create(event) != null) {
						response = Response.status(Response.Status.OK)
								.entity(MessageFormat.format(EVENT_WITH_ID_IS_ADDED_IN_DATABASE, event.getEventId()))
								.build();
					} else {
						response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
								MessageFormat.format(ERROR_PROBLEM_WITH_ADDING_EVENT_WITH_ID, event.getEventId()))
								.build();
					}
				} else {
					response = Response
							.status(Response.Status.CONFLICT).entity(MessageFormat
									.format(ERROR_EVENT_WITH_ID_ALREADY_EXISTS_IN_DATABASE, eventDto.getEventId()))
							.build();
				}
			} else {
				response = Response.status(Response.Status.BAD_REQUEST).entity(ERROR_EVENT_ID_NULL).build();
			}
		} catch (Exception e) {
			String errMessage = MessageFormat.format(ERROR_SAVING_EVENT_WITH_ID_DATABASE, eventDto.getEventId());
			logger.error(errMessage, e);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errMessage).build();
		}

		logger.debug(DEBUG_DONE_CREATING_NEW_EVENT_WITH_ID, eventDto.getEventId());
		return response;
	}

	/**
	 * Delete all events from database
	 *
	 * @return response - HTTP OK 200 for success with deleted events as JSON or
	 *         HTTP Error 500 Internal server error
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEvents() {
		logger.debug(DEBUG_STARTED_DELETING_ALL_EVENTS);
		Response response = null;

		try {
			eventDao.deleteAll();
			response = Response.status(Response.Status.OK).entity(THE_EVENTS_WERE_SUCCESSFULLY_DELETED).build();
		} catch (Exception e) {
			logger.error(ERROR_DELETING_ALL_EVENTS_DATABASE, e);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ERROR_DELETING_ALL_EVENTS_DATABASE)
					.build();
		}

		logger.debug(DEBUG_DONE_DELETING_ALL_EVENTS);
		return response;
	}

	/**
	 * Delete an event by event id
	 *
	 * @param eventId
	 *            - event id
	 * @return response - HTTP OK 200 for success, HTTP Error 404 Not Found if
	 *         event is not found or HTTP Error 500 Internal server error
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{eventId}")
	public Response deleteEvent(@PathParam("eventId") String eventId) {
		logger.debug(DEBUG_STARTED_DELETING_EVENT_WITH_ID, eventId);
		Response response = null;

		try {
			Event event = eventDao.findByEventId(eventId);

			if (event == null) {
				response = Response.status(Response.Status.NOT_FOUND)
						.entity(MessageFormat.format(ERROR_EVENT_WITH_EVENT_ID_IS_NOT_FOUND, eventId)).build();
			} else {
				Event deletedEvent = eventDao.delete(event.getId());

				if (deletedEvent != null) {
					String message = MessageFormat.format(EVENT_WITH_EVENT_ID_WAS_SUCCESSFULLY_DELETED,
							eventId);
					response = Response.status(Response.Status.OK).entity(message).build();
				} else {
					response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
							MessageFormat.format(ERROR_EVENT_WITH_EVENT_ID_IS_NOT_DELETED_SUCCESSFULLY, eventId))
							.build();
				}
			}
		} catch (Exception e) {
			String message = MessageFormat.format(ERROR_DELETING_EVENT_WITH_ID_DATABASE, eventId);
			logger.error(message, e);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
		}

		logger.debug(DEBUG_DONE_DELETING_EVENT_WITH_ID, eventId);
		return response;
	}

	private static Event convertToEntity(EventDto eventDto) {
		return modelMapper.map(eventDto, Event.class);

	}

	private List<EventDto> convertToDtoList(List<Event> events) {
		return modelMapper.map(events, new TypeToken<List<EventDto>>() {
		}.getType());
	}

}
