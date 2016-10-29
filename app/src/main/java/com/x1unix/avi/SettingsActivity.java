package com.x1unix.avi;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import android.widget.Toast;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private boolean propIsAdEnabled;
    private String propIsAdEnabledKey;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propIsAdEnabledKey = getResources().getString(R.string.avi_prop_no_ads);

        getPrefs();
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.pref_main);
        registerPropsEventHandlers();
    }

    private void registerPropsEventHandlers() {
        // Get the custom preference
        Preference customPref = (Preference) findPreference(propIsAdEnabledKey);
        customPref
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newVal) {
                        boolean switched = (boolean) newVal;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(propIsAdEnabledKey,
                                switched);
                        editor.apply();
                        return true;
                    }

                });
    }

    private void getPrefs() {
        // Get the xml/preferences.xml preferences
        preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        propIsAdEnabled = preferences.getBoolean(propIsAdEnabledKey, true);
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
