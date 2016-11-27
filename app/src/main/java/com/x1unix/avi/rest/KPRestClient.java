package com.x1unix.avi.rest;

import retrofit2.Retrofit;

import com.kinopoisk.*;

public class KPRestClient {
    public static final String BASE_URL = "https://ext.kinopoisk.ru/ios/3.11.0/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = (new NetworkApiFactory()).getClient();
        }
        return retrofit;
    }
}
