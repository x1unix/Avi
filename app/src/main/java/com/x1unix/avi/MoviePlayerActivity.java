package com.x1unix.avi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import com.x1unix.avi.helpers.AdBlocker;
import com.x1unix.avi.webplayer.*;

public class MoviePlayerActivity extends AppCompatActivity {

    private AviWebView webView;
    private String LSECTION = "MoviePlayer";
    private String currentUrl = "";
    private boolean movieLoaded = false;
    private Intent receivedIntent;

    private AviWebViewClient webClient;
    private AviWebChromeClient webChromeClient;

    private View nonVideoLayout;
    private ViewGroup videoLayout;
    private View loadingView;

    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_player);

        nonVideoLayout = findViewById(R.id.nonVideoLayout);
        videoLayout = (ViewGroup)findViewById(R.id.videoLayout);
        loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);

        // Set activity title
        receivedIntent = getIntent();
        if (receivedIntent != null) {
            setTitle(receivedIntent.getStringExtra("movieTitle"));
        }

        webView = (AviWebView) findViewById(R.id.webplayer);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!movieLoaded) {
            // Load player with intent data
            if (receivedIntent != null) {
                loadPlayer(receivedIntent.getStringExtra("movieId"));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    private void loadPlayer(String kpId) {
        AdBlocker.init(this);

        String propAdDisabled = getResources().getString(R.string.avi_prop_no_ads);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        boolean isAdBlockEnabled = preferences.getBoolean(propAdDisabled, true);

        // Initialize the AviWebChromeClient and set event handlers
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setBackgroundColor(Color.BLACK);
        webClient = new AviWebViewClient(isAdBlockEnabled);

        webChromeClient = new AviWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView);
        webChromeClient.setOnToggledFullscreen(new AviWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                if (fullscreen)
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

                    getWindow().setAttributes(attrs);

                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }

                    if (actionBar != null) actionBar.hide();
                }
                else
                {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }

                    if (actionBar != null) actionBar.show();
                }

            }
        });

        // Clean memory
        propAdDisabled = null;
        preferences = null;

        webView.setWebViewClient(webClient);
        webView.setWebChromeClient(webChromeClient);

        setMovieId(kpId);

        movieLoaded = true;
    }

    @Override
    public void onBackPressed()
    {
        // Notify the AviWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed())
        {
            if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }

    public void setMovieId(String kpId) {
        String currentLanguage = getResources().getConfiguration().locale.getLanguage();
        currentUrl = "http://avi.x1unix.com/?kpid=" + kpId + "&lang=" + currentLanguage;
        webClient.updateCurrentUrl(currentUrl);
        Log.i(LSECTION, "Loading url: " + currentUrl);
        webView.loadUrl(currentUrl);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        finishJob();
    }

    private void finishJob() {
        // Close player page
        webView.loadUrl("about:blank");
        webView.clearCache(true);
        webView = null;
        movieLoaded = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
