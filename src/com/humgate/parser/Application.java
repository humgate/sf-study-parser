package com.humgate.parser;

import org.jsoup.helper.Validate;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.*;

/**
 * Main application class. Loads logging configuration from logging.properties
 * Creates specific parser. Loads the first search results page where finds how much total search results pages exist.
 * Saves the links to each specific search result page in a collection. Then for each link to search result page loads
 * the page and adds all topics information into another collection. Finally iterates throughout topic links collection
 * loads each vacation details page and adds vacation full description from loaded vacation page inyo the vacations
 * collection. Finally saves each element og vacations collection to file
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


    // Write vacations collection to file
    private static void writeVacationsToFile(LinkedList<Vacation> vacations, String fileName, String searchString) {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy.MM.dd");

        try (BufferedWriter bufFileWriter = new BufferedWriter(new FileWriter(fileName, false))) {
            bufFileWriter.write("Vacations search results. Received " + format.format(date)+ ".\n" );
            bufFileWriter.write("Search string: " + searchString + "\n\n" );
            int i = 1;
            for (Vacation vac : vacations) {
                bufFileWriter.write(i + ". ");
                bufFileWriter.write("Text = " + vac.getVacTopicText() + "\n");
                bufFileWriter.write("URL = " + vac.getVacPageURL() + "\n");
                bufFileWriter.write("Description  = " + vac.getVacDescription() + "\n\n");
                i++;
                }
            bufFileWriter.flush();
            LOGGER.info("Successfully written vacations to file - " + fileName);
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            LOGGER.severe("Failed to write file " + fileName + " Exiting now.");
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        LOGGER.info("main method starts");
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];

        //On the search results page find the links to separate portions of search results and save to collection
        Sql_Ru_Parser sqlRuParser = new Sql_Ru_Parser();
        LinkedList<String> resultsPageNumList = sqlRuParser.getSearchResultsPagesList(url);

        //From all the topic search results pages get topic url and topic text and save as elements of VacationsList
        LinkedList<Vacation> vacationsList = sqlRuParser.getVacationTopicsList(resultsPageNumList);

        //Iterate through vacationList, read specific vacation page and read description to Vacation in VacationList
        vacationsList = sqlRuParser.getVacationDescription(vacationsList);

        //Write vacations info to file
        writeVacationsToFile(vacationsList, "results.txt", url);
    }
}


