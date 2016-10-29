package com.x1unix.avi.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

public class AviMoviePlayerWebViewClient extends WebViewClient {
    private String LSECTION = AviMoviePlayerWebViewClient.class.getName();
    private Map<String, Boolean> loadedUrls = new HashMap<>();
    private String currentUrl = "";
    private boolean disableAds = false;

    public AviMoviePlayerWebViewClient(boolean blockAds) {
        super();
        disableAds = blockAds;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.w(LSECTION, "Client has tried to redirect to '" + url + "'");
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        // Do nothing if adblock disabled
        if (!disableAds) return super.shouldInterceptRequest(view, url);

        boolean ad;
        if (!loadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            loadedUrls.put(url, ad);
        } else {
            ad = loadedUrls.get(url);
        };
        return ad ? AdBlocker.createEmptyResource("text/javascript") : super.shouldInterceptRequest(view, url);
    }

    public void onPageStarted (WebView view, String url)
    {
        if (url != currentUrl) {
            // stop loading page if its not the originalurl.
            Log.w(LSECTION, "Page loading prevented");
            view.stopLoading();
        }
    }

    public void updateCurrentUrl(String newUrl) {
        currentUrl = newUrl;
    }
}
