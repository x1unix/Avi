package com.x1unix.moonwalker;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Moonwalker {
    private Grabber grabber;
    public static String TAG = "MoonWalker";

    public Moonwalker(String referrer) {
        grabber = new Grabber(referrer);
    }

    public void getMovieByKinopoiskId(final String kinopoiskId, final Listener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String script = grabber.getPlayerScriptByKinopoiskId(kinopoiskId);
                    String frameUrl = Parser.getFrameUrlFromScript(script);

                    listener.onSuccess(frameUrl);
                } catch (Exception ex) {
                    listener.onError(ex);
                }
            }
        }).start();
    }
}
