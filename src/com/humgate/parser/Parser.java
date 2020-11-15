package com.humgate.parser;

import java.util.LinkedList;

/**
 * Defines set of methods to parse a web page which is a typical search results web page.
 */
public interface Parser {
    /**
     * Returns topic search results page URLs collection based on the search URL passed in the parameter.
     * @param searchUrl - URL string to be posted by the webpage to obtain search results.
     * @return - Collection of strings where each string is the URL of separate web page containing its own
     * portion of the topics search results .
     */
    LinkedList<String> getSearchResultsPagesList(String searchUrl);

    /**
     * Returns Vacation collection from all topic search results pages stored in parameter. Each vacation in collection must
     * have two fields filled out with data obtained from search results (topics): vacPageURL and vacTopicText.
     * vacDescription filed may be null or empty, because it will be read from vacation Details page
     * using getVacationDescription.
     * @param resultPagesLinkList - Collection of strings where each string is the URL of separate web page
     * containing its own portion of the search results
     * @return - Collection of all vacations returned by each individual search results page
     * which URL stored is the parameter
     */
    LinkedList<Vacation> getVacationTopicsList(LinkedList<String> resultPagesLinkList);

    /**
     * Iterates through all the vacations in @param. Reads the vacation detailed description form the vacation details
     * web page which Url stored in vacation vacPageURL field.
     * @param vacationsList - Collection of all vacations, where each vacation has already filled out both vacPageURL and
     * vacTopicText fields from vacation topic pages
     * @return - Collection of all vacations, where each vacation has vacDescription filled out
     */

    LinkedList<Vacation> getVacationDescription (LinkedList<Vacation> vacationsList);
}
