package com.example.xyzreader.remote;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
    public static final URL BASE_URL;
    private static String TAG = Config.class.getSimpleName();

    static {
        URL url;
        String urlString = "https://go.udacity.com/xyz-reader-json";
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
