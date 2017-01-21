package com.nlp.task.twitter.analysis.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("availabilityCheck")
public final class AvailabilityCheckEndpoint {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String availabilityCheck() {
        return "ok";
    }
}
