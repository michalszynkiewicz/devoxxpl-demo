package com.example.search;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Represents the public interface of the advertisement microservice
 * implemented in the ads module.
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 21/06/2019
 */
@Path("/ads")
@Produces(MediaType.TEXT_PLAIN)
public interface AdsClient {
   
   @GET
   @Path("{userId}")
   String getAds(@PathParam("userId") String userId);
}
