package com.x1unix.avi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import android.util.Log;

public class AviHttpRequest {
    private URL url;
    private HttpURLConnection connection;
    public AviHttpRequest(String urlPath) {
        try {
            url = new URL(urlPath);
        } catch(MalformedURLException ex) {
            // TODO: set handler for bad url
        }
    }

    public void makeGetRequest() {
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch(IOException ex) {
            // TODO: set handler for io ERROR
        }
    }
}
