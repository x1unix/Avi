package com.x1unix.avi;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.preference.Preference;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private boolean propIsAdEnabled;
    private String propIsAdEnabledKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propIsAdEnabledKey = getResources().getString(R.string.avi_prop_ads);

        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.pref_main);
        getPrefs();
    }

    private void registerPropsEventHandlers() {
        // Get the custom preference
        Preference customPref = (Preference) findPreference(propIsAdEnabledKey);
        customPref
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    public boolean onPreferenceClick(Preference preference) {
                        SharedPreferences customSharedPreference = getSharedPreferences(
                                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = customSharedPreference
                                .edit();
                        editor.putBoolean(propIsAdEnabledKey,
                                "The preference has been clicked");
                        editor.apply();
                        return true;
                    }

                });
    }

    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        propIsAdEnabled = prefs.getBoolean(propIsAdEnabledKey, true);
    }
}
