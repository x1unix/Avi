package com.x1unix.avi;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MoviePlayerActivity extends AppCompatActivity {

    private WebView webView;
    private String LSECTION = "MoviePlayer";
    private String currentUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_player);

        webView = (WebView) findViewById(R.id.webplayer);

        Intent receivedIntent = getIntent();

        // Load player with intent data
        if (receivedIntent != null) {
            loadPlayer(receivedIntent.getStringExtra("movieId"),
                    receivedIntent.getStringExtra("movieTitle"));
        }
    }

    private void loadPlayer(String kpId, String title) {
        setTitle(title);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.w(LSECTION, "Client has tried to redirect to '" + url + "'");
                return true;
            }

            public void onPageStarted (WebView view, String url)
            {
                if (url != currentUrl) {
                    // stop loading page if its not the originalurl.
                    Log.w(LSECTION, "Page loading prevented");
                    view.stopLoading();
                }
            }
        });

        setMovieId(kpId);
    }

    public void setMovieId(String kpId) {
        currentUrl = "http://sandbx.ml/?kpid=" + kpId;
        Log.i(LSECTION, "Loading url: " + currentUrl);
        webView.loadUrl(currentUrl);
    }
}
