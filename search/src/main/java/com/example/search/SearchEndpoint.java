package com.example.search;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * The search engine REST endpoint
 * that enables looking for pages based on given keywords.
 */
@Path("/search")
public class SearchEndpoint {

    /**
     * A reactive Stream that enables sending to a Kafka server,
     * all keywords provided by each user in every search.
     * The ads service is notified and then stores
     * those keywords to provide contextual ads for every user.
     */
    @Stream("search-terms")
    Emitter<JsonObject> queryEmitter;

    /**
     * Searches pages based on provided keywords
     * @param userId the id of the user performing the search
     * @param query the list of keywords separated by a blank space
     * @return a List containing the URI of the pages found
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> search(@QueryParam("userId") String userId,
                               @QueryParam("query") String query) {
        if (query == null || userId == null) {
            return Collections.emptyList();
        }

        queryEmitter.send(
              new JsonObject()
              .put("userId", userId)
              .put("query", query)
        );

        List<String> keywords = asList(query.split("[^\\w]+"));
        PanacheQuery<PageKeyword> dbQuery =
              PageKeyword.find("keyword in ?1", keywords);


        return dbQuery.stream()
              .map(pk -> pk.page)
              .distinct()
              .collect(Collectors.toList());
    }
}