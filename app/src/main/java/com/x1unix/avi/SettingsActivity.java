package com.x1unix.avi;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private boolean propIsAdEnabled;
    private boolean propIsAutoupdate;

    private String propIsAdEnabledKey;
    public String propIsAutoupdateKey;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propIsAdEnabledKey = getResources().getString(R.string.avi_prop_no_ads);
        propIsAutoupdateKey = getResources().getString(R.string.avi_prop_autocheck_updates);

        getPrefs();
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.pref_main);
        registerPropsEventHandlers();
    }

    private void registerPropsEventHandlers() {
        // Get the custom preference
        Preference customPref = (Preference) findPreference(propIsAdEnabledKey);
        customPref.setOnPreferenceChangeListener(onTogglePreferenceListener);

        Preference autoUpdatePref = (Preference) findPreference(propIsAutoupdateKey);
        autoUpdatePref.setOnPreferenceChangeListener(onTogglePreferenceListener);
    }

    private Preference.OnPreferenceChangeListener onTogglePreferenceListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newVal) {
            boolean switched = (boolean) newVal;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(preference.getKey(),
                    switched);
            editor.apply();
            return true;
        }
    };

    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        propIsAdEnabled = preferences.getBoolean(propIsAdEnabledKey, true);
        propIsAutoupdate = preferences.getBoolean(propIsAutoupdateKey, true);
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
