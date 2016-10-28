package com.x1unix.avi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.Preference;


public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        setContentView(R.layout.activity_settings);
    }
}
