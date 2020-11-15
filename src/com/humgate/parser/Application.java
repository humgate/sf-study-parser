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

    // Init global package-wide logger
    private static final Logger LOGGER = Logger.getLogger("com.humgate.parser");

    public static void main(String[] args) {
        LOGGER.info("main method starts");
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];

        //On the search results page find the links to separate portions of search results and save to collection
        Sql_Ru_Parser sqlRuParser = new Sql_Ru_Parser();
        LinkedList<String> resultsPageNumList = sqlRuParser.getSearchResultsPagesList(url);

        //From all the topic search results pages get topic url and topic text and save as elements of VacationsList
        LinkedList<Vacation> vacationsList = sqlRuParser.getVacationTopicsList(resultsPageNumList);

        vacationsList = sqlRuParser.getVacationDescription(vacationsList);
        for (Vacation vac : vacationsList) {
             System.out.println("URL = " + vac.getVacPageURL());
             System.out.println("Text = " + vac.getVacTopicText());
             System.out.println("Description  = " + vac.getVacDescription());
        }
    }
}


