package com.example.search;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * A advertisement JAX-RS endpoint that just dispatches request from
 * the front-end to the actual ads microservice.
 * The URI of the actual service is provided in the application.properties
 * and configured here using Microprofile Configuration.
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 21/06/2019
 */
@Path("/ads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Ads {

   @ConfigProperty(name = "adsServiceUri")
   URI adsServiceUri;

   /**
    * A JAX-RS client that dispatches the request
    * to the actual ads service.
    */
   AdsClient adsClient;

   @GET
   @Path("{userId}")
   @Fallback(AdsFailureHandler.class)
   public String gimmeAds(@PathParam("userId") String userId) {
      return adsClient.getAds(userId);
   }


   @PostConstruct
   public void initialize() {
      adsClient = RestClientBuilder.newBuilder()
            .baseUri(adsServiceUri)
            .build(AdsClient.class);
   }

   static class AdsFailureHandler implements FallbackHandler<String> {
      @Override
      public String handle(ExecutionContext context) {
         context.getFailure().printStackTrace();
         return "[Fallback ad] Devoxx is awesome!";
      }
   }
}
