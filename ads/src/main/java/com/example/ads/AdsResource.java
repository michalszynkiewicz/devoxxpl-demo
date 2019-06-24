package com.example.ads;

import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Path("/ads")
public class AdsResource {

   private final Map<String, List<String>> keywordsByUser = new ConcurrentHashMap<>();

   @Inject
   AdStorage adStorage;

   @Incoming("queries")
   public void consume(JsonObject message) {
      System.out.println("got user query: " + message);
      String userId = message.getString("userId");
      String query = message.getString("query");
      if (userId == null || query == null) {
         return;
      }
      String[] keywords = query.split("[^\\w]+");
      Arrays.stream(keywords)
            .filter(k -> !k.isEmpty())
            .forEach(k -> addKeyword(userId, k));
   }

   private void addKeyword(String userId, String k) {
      keywordsByUser.computeIfAbsent(userId, key -> new ArrayList<>())
            .add(k);
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   @Path("{userId}")
   public String getAd(@PathParam("userId") String userId) {
      List<String> userKeywords = keywordsByUser.getOrDefault(userId, Collections.emptyList());

      return adForKeywords(userKeywords);
   }

   private String adForKeywords(List<String> userKeywords) {
      List<String> ads = userKeywords.stream()
            .map(adStorage::get)
            .flatMap(List::stream)
            .collect(Collectors.toList());

      if (ads.size() == 0) {
         return "[default ad] Have you been at the Red Hat booth?";
      }
      int adIndex = new Random().nextInt(ads.size());
      return ads.get(adIndex);
   }


}