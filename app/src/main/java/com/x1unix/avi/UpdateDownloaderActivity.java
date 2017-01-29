package com.x1unix.avi;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UpdateDownloaderActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    private TextView txUpdateProgress;

    private String mock = "<h2>Changelog</h2>\n" +
            "\n" +
            "<ul>\n" +
            "<li>Allow fullscreen mode for webplayer</li>\n" +
            "<li>Fix error with start activity on some devices</li>\n" +
            "<li>Use SVG icons for splash screen</li>\n" +
            "<li>Update splash screen</li>\n" +
            "<li>Prevent load Rollbar for debug mode</li>\n" +
            "</ul>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_downloader);
        initView();

        loadUpdateInformation();
    }

    private void initView() {
        txUpdateProgress = (TextView) findViewById(R.id.avi_update_progress);

        initProgressBar();
        initWebView();
    }

    private void loadUpdateInformation() {
        webView.loadData(getDecoratedChangelogHTML(mock), "text/html", "utf-8");
    }

    private String getDecoratedChangelogHTML(String changelogHTML) {
        String result = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("changelog_template.html")));

            // do reading, usually loop until end of file reading
            String mLine;
            String mTemplate = "";
            while ((mLine = reader.readLine()) != null) {
                mTemplate += mLine;
            }

            result = mTemplate.replaceAll("%CONTENT%", changelogHTML);

        } catch (IOException e) {
            //log the exception
            result = changelogHTML;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    result = changelogHTML;
                }
            }
        }
        return result;
    }

    private void initProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.avi_update_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorAccentDark),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.colorAccentDark),
                android.graphics.PorterDuff.Mode.SRC_IN);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.avi_update_changelog);
        webView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDefault));
        webView.setVisibility(View.VISIBLE);
    }
}
