package com.x1unix.moonwalker;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.HEAD;

public class Grabber {
    private String referrer;
    private OkHttpClient client;
    private RequestInterceptor interceptor;

    private static final String HEAD_CSRF_TOKEN = "X-CSRF-Token";
    private static final String HEAD_RAY = "X-Bool-Ray";
    private static final String HEAD_RAY_VALUE = "XRAY";

    private static final String SESSION_URL = "/sessions/new_session";

    public Grabber(String referrer) {
        this.referrer = referrer;
        this.client = this.getClient();
    }

    public String getPlayerScriptByKinopoiskId(String kpId) throws IOException, MoonException {
        String url = "http://moonwalk.co/player_api?kp_id=" + kpId;
        return this.getResource(url);
    }


    public String getPlaylist(MoonSession session) throws IOException, MoonException {

        RequestBody formBody = new FormBody.Builder()
                .add(MoonSession.JSON_PROP_VIDEO_TOKEN, session.getVideoToken())
                .add(MoonSession.JSON_PROP_CONTENT_TYPE, session.getContentType())
                .add(MoonSession.JSON_PROP_KEY, session.getKey())
                .add(MoonSession.JSON_PROP_PID, session.getPid())
                .add(MoonSession.JSON_PROP_P_DOMAIN_ID, session.getDomainId())
                .add(MoonSession.JSON_PROP_AD_ATTR, session.getAdAttr())
                .add(MoonSession.JSON_PROP_DEBUG, session.isDebug())
                .build();

        Request request = new Request.Builder()
                .url(session.getHost() + SESSION_URL)
                .addHeader(HEAD_CSRF_TOKEN, session.getCSRFToken())
                .addHeader(HEAD_RAY, HEAD_RAY_VALUE)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            int code = response.code();
            throw new MoonException("Failed to get playlist using session, HTTP Code: " + code );
        }

        return response.body().string();
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
