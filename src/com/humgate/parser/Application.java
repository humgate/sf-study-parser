package com.humgate.parser;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
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
    private static final Logger LOGGER= Logger.getLogger("com.humgate.parser");

    public static void main(String[] args) {
        LOGGER.info("main method starts");
        Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
        System.out.println("Fetching %s..." + url);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            LOGGER.info("got html page into jsoup documents");
        } catch (IOException e) {
            System.out.println("Failed to get the webpage from URL");
        }


        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");


        System.out.print("\nMedia: (" + media.size () + ")");
        for (Element src : media) {
            if (src.normalName().equals("img"))
                System.out.println(" * %s: <%s> %sx%s (%s)" +  src.tagName() +
                                src.attr("abs:src") +  src.attr("width") +
                                src.attr("height") + (src.attr("alt")));
            else
            System.out.println(" * %s: <%s>" + src.tagName() + src.attr("abs:src"));
        }


        LOGGER.info("main method ends");
    }
}


