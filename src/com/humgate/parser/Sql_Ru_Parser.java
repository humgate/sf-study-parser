package com.humgate.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.*;

public class Sql_Ru_Parser implements Parser {
    //This constant will be used as classname to find result pages list on topics page
    static final String RESULTS_PAGES_LIST_CLASS = "forumtable_results";
    //This constant will be used as classname to topic topics page
    static final String TOPIC_CLASS = "postslisttopic";
    //This constant will be used as classname to Vacation description on vacation web page
    static final String VACATION_DESCRIPTION_CLASS = "msgBody";
    private static final Logger LOGGER = Logger.getLogger("com.humgate.parser");

    @Override
    public LinkedList<String> getSearchResultsPagesList(String searchUrl) {
        //Connect to the searchUrl using jsoup
        Document doc = null;
        try {
            doc = Jsoup.connect(searchUrl).get();
        } catch (IOException e) {
            System.out.println("Failed to get the webpage from URL");
        }
        LOGGER.info("Successfully fetched base url: " + searchUrl);

        // Searching for second instance of <table class="forumtable_results">. It contains needed links
        Elements resultPagesNumList = null;
        LinkedList<String> resultPagesLinksList = new LinkedList<>();
        try {
            resultPagesNumList = doc.getElementsByClass(RESULTS_PAGES_LIST_CLASS);
            LinkedList<String> searchResultPagesLinks = new LinkedList<>();
        } catch (NullPointerException npe) {
            LOGGER.severe("Failed to find " + RESULTS_PAGES_LIST_CLASS + "on the page: " + searchUrl +
                    "Exiting now.");
            System.exit(2);
        }

        //Getting page links from page number elements. Adding to vacations collection
        Element targetEl;
        for (Element el : resultPagesNumList) {
            targetEl = el.selectFirst("td");
            if (targetEl.text().startsWith("По вашему запросу найдено")) {
                LOGGER.info("Total vacation topics found: " + targetEl.child(0).text());
            }
            if (targetEl.text().startsWith("Страницы:")) {
                /*the first page in the list is not an URL, just text == "1", so in the first collection element we
                 * store base page URL
                 */
                resultPagesLinksList.add(searchUrl);

                //starting with 2nd all remaining numbers on the page are links, so get them from the "href" attr
                for (int i = 1; i < targetEl.childrenSize(); i++) {
                    resultPagesLinksList.add(targetEl.child(i).absUrl("href"));
                }
                LOGGER.info("Search results page count successfully parsed: " +
                        targetEl.child(targetEl.childrenSize() - 1).text());
                break;
            }
        }
        for (String str : resultPagesLinksList) {
            System.out.println(str);
        }
        return resultPagesLinksList;
    }

    @Override
    public LinkedList<Vacation> getVacationTopicsList(LinkedList<String> resultPagesLinkList) {
        /*
         * Load each page with search results and save each topic name and vacation url to the
         * collection of Vacations
         */
        LinkedList<Vacation> vacationsList = new LinkedList<>();

        Document doc = null;
        for (String s : resultPagesLinkList) {
            try {
                doc = Jsoup.connect(s).get();
                LOGGER.info("Successfully fetched url: " + s);
            } catch (IOException e) {
                LOGGER.severe("Failed to fetch url" + s + "Exiting now.");
                System.exit(2);
            }

            try {
                // <td class="postslisttopic"> contains our topic, create the list of those
                Elements targetPageClassList = doc.getElementsByClass(TOPIC_CLASS);

                // Iterate through all <td class="postslisttopic"> on the page, get vacation detailed page link
                for (Element el : targetPageClassList) {
                    //cut trailing "?hl=java" it is not needed
                    String tmpStr = el.child(0).absUrl("href").replaceAll("\\?hl=java", "");
                    vacationsList.add(new Vacation(tmpStr, el.selectFirst("a[href]").ownText(), null));
                }
            } catch (NullPointerException npe) {
                LOGGER.severe("No " + TOPIC_CLASS + "class elements found on the html page. Exiting now.");
                System.exit(2);
            }
            LOGGER.info("Successfully read topics data from : " + s );
        }
        return vacationsList;
    }

    @Override
    public LinkedList<Vacation> getVacationDescription(LinkedList<Vacation> vacationsList) {
        Document doc = null;
        // For each element of vacationsList read its Description from its own details page
        for (int i = 0;  i < vacationsList.size(); i++) {
            Vacation vac = vacationsList.get(i);
            try {
                doc = Jsoup.connect(vac.getVacPageURL()).get();
                Elements msgBodyList = doc.getElementsByClass(VACATION_DESCRIPTION_CLASS);
                vac.setVacDescription(msgBodyList.get(1).selectFirst("td").text());
            } catch (IOException e) {
                LOGGER.severe("Failed to get the webpage from URL " + vac.getVacPageURL() + "Exiting now.");
                System.exit(2);
            } catch (NullPointerException npe) {
                LOGGER.severe("No " + VACATION_DESCRIPTION_CLASS + "class elements found on the page. " +
                        "Exiting now.");
                System.exit(2);
            }
            vacationsList.set(i, vac);
            // Log the read progress each 50 vacations and when done
            if (i%50 == 0 && i > 0) {
                LOGGER.info("Successfully read Description for " + i + "vacations");
            }
            if (i == vacationsList.size() - 1) {
                LOGGER.info("Successfully read Description for all " + i + "vacations");
            }
        }
        return vacationsList;
    }
}
