package com.example.xyzreader.ui;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Provides formatted date string for the article titles
 */

public class XyzDateUtils {

    private static final String TAG = XyzDateUtils.class.getSimpleName();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private final SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, "parse published date exception: " + ex.getMessage());
            return new Date();
        }
    }

    private String getFormattedTimeString(long timeInMills) {
        return DateUtils.getRelativeTimeSpanString(
                timeInMills,
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString();
    }

    private String getDetailsStringAfterEpoch(Date publishedDate) {
        return getFormattedTimeString(publishedDate.getTime());
    }

    // If date is before 1902, just show the string
    private String getDetailsStringPriorEpoch(Date publishedDate) {
        return outputFormat.format(publishedDate);
    }

    public String getDateString(String publishedDateString) {
        Date publishedDate = parsePublishedDate(publishedDateString);
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            return getDetailsStringAfterEpoch(publishedDate);
        } else {
            return getDetailsStringPriorEpoch(publishedDate);
        }
    }
}
