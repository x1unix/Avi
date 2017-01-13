package com.x1unix.avi;

import android.os.Build;

import com.yandex.metrica.YandexMetrica;
public class Application extends android.app.Application {
    protected final String AM_KEY = "7968f25e-5d6b-4cb3-be18-069f2bf8bd8b";

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            // AppMetrica SDK
            YandexMetrica.activate(getApplicationContext(), AM_KEY);
            YandexMetrica.enableActivityAutoTracking(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                YandexMetrica.enableActivityAutoTracking(this);
            }
        }
    }
}
