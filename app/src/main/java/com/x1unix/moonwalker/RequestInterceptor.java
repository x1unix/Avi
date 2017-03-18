package com.x1unix.moonwalker;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class RequestInterceptor implements Interceptor {
    private String refererUrl;
    private String originalReferer;

    public RequestInterceptor(String referer) {
        refererUrl = referer;
        originalReferer = referer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request interceptedRequest = chain.request();
        Request decoratedRequest = decorateRequest(interceptedRequest);
        return chain.proceed(decoratedRequest);
    }

    public void setRefererUrl(String newUrl) {
        refererUrl = newUrl;
    }

    public void resetRefererUrl() {
        this.refererUrl = originalReferer;
    }

    public String getUserAgent() {
        return "Mozilla/5.0 (Linux; Android 4.4.2; Nexus 4 Build/KOT49H) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.114 " +
                "Mobile Safari/537.36";
    }

    private Request decorateRequest(Request request) {
        Request.Builder decoratedRequest = request.newBuilder();

        decoratedRequest
                .removeHeader("User-Agent")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                // .addHeader("Accept-Encoding", "gzip, deflate, sdch")
                .addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4,uk;q=0.2,be;q=0.2,de;q=0.2,pt;q=0.2")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Referer", refererUrl)
                .addHeader("Pragma", "no-cache")
                .addHeader("User-Agent", getUserAgent());

        return decoratedRequest.build();
    }
}
