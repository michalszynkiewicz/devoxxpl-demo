package com.example.search;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

/**
 * Provides the metadata for pages that can be searched
 * using the search engine exposed by {@link SearchEndpoint}.
 *
 * @author Michal Szynkiewicz, michal.l.szynkiewicz@gmail.com
 * <br>
 * Date: 24/06/2019
 */
@Entity
public class PageKeyword extends PanacheEntity {
   /**
    * The page URI.
    */
   public String page;

   /**
    * The keyword associated to the page,
    * enabling to find such a page by that keyword.
    */
   public String keyword;
}
