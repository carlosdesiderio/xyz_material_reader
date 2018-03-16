package com.example.xyzreader.remote;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL;
    private static String TAG = Config.class.getSimpleName();
    private static String URL_STRING_ORIGINAL = "https://go.udacity.com/xyz-reader-json";
    private static String URL_STRING_REVIEW = "https://raw.githubusercontent.com/TNTest/xyzreader/master/data.json";

    static {
        URL url;
        String urlString = URL_STRING_REVIEW;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ignored) {
            String msg = "Malformed URL String: " + urlString;
            // throws a chained exception
            throw new IllegalArgumentException(msg, ignored);
        }

        BASE_URL = url;
    }
}
