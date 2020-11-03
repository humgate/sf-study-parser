package com.humgate.parser;

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
        LOGGER.info("main method ends");
    }
}


