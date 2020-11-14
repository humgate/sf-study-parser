package com.humgate.parser;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.logging.*;

/**
 * Main application class. Loads logging configuration from logging.properties
 */
public class Application {

    static {
        //Read logging.properties
        InputStream stream = Application.class.getClassLoader().getResourceAsStream("logging.properties");
        if (stream == null) {
            System.out.println("Critical error: logging properties file not found. Exiting now");
            System.exit(2);
        }

        //read logging config if the resource found
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.out.println("Failed to read logging properties from file. Will now exit");
            System.exit(2);
        }
    }

    // Init global package wide logger
    private static final Logger LOGGER = Logger.getLogger("com.humgate.parser");

    public static void main(String[] args) {
        LOGGER.info("main method starts");
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];

        LOGGER.info("Started url fetching:  " + url);

        //подключаемся и получаем страницу
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println("Failed to get the webpage from URL");
        }
        LOGGER.info("Successfully fetched base url: " + url);

        /* На странице находим сколько всего страниц вернул поиск по переданному url
         * сохраняем это число как int.
         */
        Element targetEl;
        int pagesCount = 0;
        Elements searchPagesNumList = doc.getElementsByClass("forumtable_results");

        for (Element el : searchPagesNumList) {
            targetEl = el.selectFirst("td");
            System.out.println(targetEl.child(0).text());
            if (targetEl.text().startsWith("По вашему запросу найдено")) {
                LOGGER.info("Total vacation topics found: " + targetEl.child(0).text());
            }
            if (targetEl.text().startsWith("Страницы:")) {
                pagesCount = Integer.parseInt(targetEl.child(targetEl.childrenSize() - 1).text());
                LOGGER.info("Search results page count successfully parsed: " + pagesCount);
                break;
            }
        }

        /*
         * Load each page with search results and save each topic name and vacation url to the
         * collection of Vacations
         */
        LinkedList<Vacation> vacationsList = new LinkedList<>();

        for (int i = 1; i <= pagesCount; i++) {
            try {
                if (i > 1) {
                    try {
                        doc = Jsoup.connect(url + "&pg=" + i).get();
                    } catch (IOException e) {
                        LOGGER.severe("Failed to get the webpage from URL " + (url + "&pg=" + i));
                    }
                    LOGGER.info("Successfully fetched base url: " + url);
                }
                Elements targetPageClassList = doc.getElementsByClass("postslisttopic");
                for (Element el : targetPageClassList) {
                    String tmpStr =
                            // cut "?hl=java" form the tail of the href, just no need for it
                            el.selectFirst("a[href]").absUrl("href").replaceAll("\\?hl=java", "");
                    vacationsList.add(new Vacation(tmpStr, el.selectFirst("a[href]").ownText(), null));
                }
                System.out.print("Topicscount: (" + targetPageClassList.size() + ")\n");
            } catch (NullPointerException npe) {
                LOGGER.info("No postslisttopic class elements found on the html page");
            }
        }
        System.out.println(vacationsList.size());
        for (Vacation vac : vacationsList) {
            System.out.println(vac.getVacPageURL());
        }

       /*
        * Iterate through vacations collection. Load the vacation details page from the Url stored in vacation from
        * vacation url field. The description we want located in the <td class="msgBody"> counting from document begin
        * so it has index 1 in getElementsByClass("msgBody") collection
        */
        try {
            doc = Jsoup.connect(vacationsList.get(1).getVacPageURL()).get();
        } catch (IOException e) {
            LOGGER.severe("Failed to get the webpage from URL " + vacationsList.get(1).getVacPageURL());
        }

        LOGGER.info("Successfully fetched base url: " + url);

        Elements msgBodyList = doc.getElementsByClass("msgBody");
        targetEl = msgBodyList.get(1).selectFirst("td");
        System.out.println(targetEl.text());
    }
}


