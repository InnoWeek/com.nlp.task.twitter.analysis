package com.nlp.task.twitter.analysis.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nlp.task.twitter.analysis.dao.TweetDao;

@Path("tweets")
public class TweetService {

	private TweetDao tweetDao = new TweetDao();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTweets() {
		
		return Response.ok(tweetDao.readAll()).build();
	}
	
	@GET
	@Path("unknown")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnkownTweets() {
		
		return Response.ok(tweetDao.findAllUnknown()).build();
	}
}
