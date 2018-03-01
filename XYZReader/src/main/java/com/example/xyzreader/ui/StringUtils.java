package com.example.xyzreader.ui;

import android.support.annotation.NonNull;

/**
 * Provides formatted string for the article titles
 */

public class StringUtils {


    private XyzDateUtils dateUtils;

    public StringUtils() {
        this.dateUtils = new XyzDateUtils();
    }

    @NonNull
    public String getListItemSubtitleString(String dateString, String author) {
        String formattedDateString = dateUtils.getDateString(dateString);
        return formattedDateString + "\n by " + author;
    }

    @NonNull
    public String getArticleDetailDateString(String dateString) {
        return dateUtils.getDateString(dateString)  + " by ";
    }
}
