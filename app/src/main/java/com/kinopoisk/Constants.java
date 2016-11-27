package com.kinopoisk;

public final class Constants {
    public static final String API_VERSION = "3.11.0";
    public static final String KINOPOISK_ENDPOINT = "https://ext.kinopoisk.ru/ios/";
    public static String getPosterUrl(String kpId) {
        return "http://st.kp.yandex.net/images/film_iphone/iphone360_" + kpId + ".jpg";
    }
}