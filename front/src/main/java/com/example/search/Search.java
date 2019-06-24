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
import java.util.List;

import static java.util.Arrays.asList;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Search {

   @ConfigProperty(name = "searchServiceUri")
   URI searchServiceUri;

   SearchClient searchClient;

   @GET
   @Path("{userId}/{query}")
   @Fallback(SearchFallbackHandler.class)
   public List<String> search(@PathParam("userId") String userId,
                              @PathParam("query") String query) {
      return searchClient.getPages(query, userId);
   }

   @PostConstruct
   public void initialize() {
      searchClient = RestClientBuilder.newBuilder()
            .baseUri(searchServiceUri)
            .build(SearchClient.class);
   }

   static class SearchFallbackHandler implements FallbackHandler<List<String>> {
      @Override
      public List<String> handle(ExecutionContext context) {
         context.getFailure().printStackTrace();
         return asList("Fallback", "http://twitter.com/mszynkiewicz");
      }
   }

}