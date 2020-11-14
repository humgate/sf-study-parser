package com.humgate.parser;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

        //подключаемся и получаем страницу
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            LOGGER.info("got html page into jsoup documents");
        } catch (IOException e) {
            System.out.println("Failed to get the webpage from URL");
        }

        /* На странице находим сколько всего страниц вернул поиск по переданному url
        * сохраняем это число как int.
        */
        Element targetEl = null;
        Elements searchPagesNumList = doc.getElementsByClass("forumtable_results" );
        for (Element el: searchPagesNumList) {
            System.out.println(el.child(0).child(0).selectFirst("td").ownText());
            targetEl = el.child(0).child(0).selectFirst("td");
            if (targetEl.ownText().equals("Страницы: ..")) {
                System.out.println("нашли!");
            break;
            }
        }

        String str = targetEl.child(targetEl.childrenSize()-1).text();
        int pagesCount = Integer.parseInt(str);
        System.out.println(pagesCount);


        /*
        //По тексту страницы ищем вакансии element class="postslisttopic" и сохранаем их список в коллекцию
        try {
            Elements vacElmList = doc.getElementsByClass("postslisttopic");
            LinkedList<Vacation> vacsList= new LinkedList<>();
            for (Element el: vacElmList) {
                String tmpStr =
                        // cut "?hl=java" form the tail of the href
                        el.selectFirst("a[href]").absUrl("href").replaceAll("\\?hl=java","");
                vacsList.add(new Vacation(tmpStr, el.selectFirst("a[href]").ownText(),null));
            }
            System.out.print("Topicscount: (" + vacElmList.size () + ")\n");
            for (Vacation vac : vacsList) {
                System.out.println(vac.getVacPageURL());
           }

        } catch (NullPointerException npe) {
            LOGGER.info("No postslisttopic class elements found on the html page");          
        }
        */
        /* проходим по каждому элементу коллекции вакансий и скачиваем страницу вакансии, извлекаем текст
         и сохраняем в атрибуте коллекции
         */

        LOGGER.info("main method ends");
    }
}


