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

    public void getMovieByKinopoiskId(String kinopoiskId, final Listener listener) {
        grabber.getPlayerScriptByKinopoiskId(kinopoiskId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(Moonwalker.TAG, e.getMessage());
                listener.onError(e, call);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();

                try {
                    if (Grabber.isSuccessful(response)) {
                        String txt = response.body().string();
                        String frameUrl = Parser.getFrameUrlFromScript(txt);

                        listener.onSuccess(frameUrl, response);
                    } else {
                        listener.onError(new MoonException("Failed to get script, HTTP error " + code), call);
                    }
                } catch (Exception ex) {
                    listener.onError(ex, null);
                }
            }
        });
    }
}
