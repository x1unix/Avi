package com.x1unix.avi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SupportActivity extends AppCompatActivity {

    private String[] langs = new String[]{"ru", "uk"};
    private List<String> langsList = new ArrayList<String>();
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        langsList.addAll(Arrays.asList(langs));

        setContentView(R.layout.activity_support);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        webView = (WebView) findViewById(R.id.webview_support);
        Locale cLocale = getResources().getConfiguration().locale;
        String cLanguage = cLocale.getLanguage();

        String fileLangPrefix = (langsList.contains(cLanguage)) ? cLanguage : "en";
        webView.loadUrl("file:///android_asset/help-" + fileLangPrefix + ".html");

        cLocale = null;
        cLanguage = null;
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
