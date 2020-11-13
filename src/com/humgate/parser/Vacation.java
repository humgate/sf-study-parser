package com.humgate.parser;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Vacation {

    private String vacPageURL;
    private String vacTopicText;
    private String vacDescription;

    public Vacation(String vacPageURL, String vacTopicText, String vacDescription) {
        this.vacPageURL = vacPageURL;
        this.vacTopicText = vacTopicText;
        this.vacDescription = vacDescription;
    }
}
