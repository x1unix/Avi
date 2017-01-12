package com.x1unix.avi;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.rollbar.android.Rollbar;
import com.yandex.metrica.YandexMetrica;

public class MainActivity extends AppCompatActivity {

    protected int SPLASH_DISPLAY_LENGTH = 500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        if (!BuildConfig.DEBUG) {
            // Init Rollbar monitor on release
            Rollbar.init(this, "b2769f6943314e52be560fe9c2ab7626", "production");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(MainActivity.this, NewDashboardActivity.class);
                MainActivity.this.startActivity(mainIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
