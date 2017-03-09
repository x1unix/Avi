package com.x1unix.moonwalker;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Grabber {
    private String referrer;
    private OkHttpClient client;
    private RequestInterceptor interceptor;

    public Grabber(String referrer) {
        this.referrer = referrer;
        this.client = this.getClient();
    }

    public String getPlayerScriptByKinopoiskId(String kpId) throws IOException, MoonException {
        String url = "http://moonwalk.co/player_api?kp_id=" + kpId;
        return this.getResource(url);
    }

    public String getResource(String url)  throws IOException, MoonException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response resp = client.newCall(request).execute();

        if (!Grabber.isSuccessful(resp)) {
            throw new MoonException("Failed to get script, HTTP error " + resp.code());
        }

        updateReferer(url);

        return resp.body().string();
    }

    private void updateReferer(String referer) {
        interceptor.setRefererUrl(referer);
    }

    public void resetState() {
        interceptor.resetRefererUrl();
    }

    private static boolean isSuccessful(Response response) {
        int code = response.code();
        return (code >= 200) && (code < 400);
    }

    private OkHttpClient getClient() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        interceptor = new RequestInterceptor(referrer);

        return new OkHttpClient().newBuilder()
                .addInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
