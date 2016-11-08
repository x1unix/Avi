package com.x1unix.avi.updateManager;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTARestClient {
    public static final String BASE_URL = "http://avi.x1unix.com";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
