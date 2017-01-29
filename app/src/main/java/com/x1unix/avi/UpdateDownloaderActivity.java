package com.x1unix.avi;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.x1unix.avi.model.AviSemVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UpdateDownloaderActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private WebView webView;

    private TextView txUpdateProgress;
    private TextView txUpdateTag;
    private AviSemVersion updatePkg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_downloader);

        final Intent intent = getIntent();

        if (intent != null) {
            updatePkg = (AviSemVersion) intent.getSerializableExtra("update");
            if (updatePkg != null) {
                initView();
                loadUpdateInformation();
            }
        }
    }

    private void initView() {
        txUpdateProgress = (TextView) findViewById(R.id.avi_update_progress);
        txUpdateTag = (TextView) findViewById(R.id.avi_update_tag);

        initProgressBar();
        initWebView();
    }

    private void loadUpdateInformation() {

        txUpdateTag.setText(updatePkg.getTag());

        if (updatePkg.hasChangelog()) {
            webView.loadData(getDecoratedChangelogHTML(updatePkg.getChangelog()), "text/html", "utf-8");
        }
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
