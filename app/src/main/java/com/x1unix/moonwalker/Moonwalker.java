package com.x1unix.moonwalker;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Facade for Moonwalker API
 */
public class Moonwalker {
    private Grabber grabber;
    public static String TAG = "MoonWalker";
    public static String version = "0.1.0";

    public Moonwalker(String referrer) {
        grabber = new Grabber(referrer);
    }

    public void getMovieByKinopoiskId(final String kinopoiskId, final Listener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get embedded script content
                    String script = grabber.getPlayerScriptByKinopoiskId(kinopoiskId);

                    // Get iframe url
                    String frameUrl = Parser.getFrameUrlFromScript(script);

                    // Get iframe source
                    String iframeHtml = grabber.getResource(frameUrl);

                    // Get player's frame source
                    String playerFrameSrc = Parser.getPlayerFrameUrlFromHtml(iframeHtml);

                    // Create moon session
                    MoonSession session = MoonSession.fromPlayerUrl(playerFrameSrc, grabber);

                    ManifestCollection manifests = session.getPlaylist();

                    listener.onSuccess(manifests.getM3u8Manifest());

                    grabber.resetState();
                } catch (Exception ex) {
                    listener.onError(ex);
                }
            }
        }).start();
    }

    public static String getVersion() {
        return Moonwalker.version;
    }
}
