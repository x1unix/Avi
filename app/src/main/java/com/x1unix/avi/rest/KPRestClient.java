package com.x1unix.avi.rest;

import com.x1unix.avi.kp.KinopoiskRequestInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KPRestClient {
    public static final String BASE_URL = "https://ext.kinopoisk.ru/ios/3.11.0/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            KinopoiskRequestInterceptor interceptor = new KinopoiskRequestInterceptor();
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
