package com.x1unix.avi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.x1unix.avi.model.AviSemVersion;
import com.x1unix.avi.updateManager.OTAStateListener;
import com.x1unix.avi.updateManager.OTAUpdateChecker;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends AppCompatPreferenceActivity {

    private boolean propIsAdEnabled;
    private boolean propIsAutoupdate;

    private String propIsAdEnabledKey;
    private String propIsAutoupdateKey;
    private String propUpdateNow;
    private String propAllowUnstable;
    private SharedPreferences preferences;

    private Resources res;

    private final String APP_URL = "http://avi-app.x1unix.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();

        propIsAdEnabledKey = getResources().getString(R.string.avi_prop_no_ads);
        propIsAutoupdateKey = getResources().getString(R.string.avi_prop_autocheck_updates);
        propUpdateNow = getResources().getString(R.string.prop_btn_update_now);
        propAllowUnstable = getResources().getString(R.string.avi_prop_allow_unstable);

        getPrefs();
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.pref_main);

        boolean isPreview = res.getBoolean(R.bool.isPreviewVersion);
        String currentVersion = BuildConfig.VERSION_NAME;

        if (isPreview) {
            currentVersion += " - " + res.getString(R.string.preview_version);
        }

        ((Preference) findPreference("avi_app_version")).setSummary(currentVersion);
        ((Preference) findPreference("avi_app_build")).setSummary(String.valueOf(BuildConfig.VERSION_CODE));

        registerPropsEventHandlers();
    }



    private void registerPropsEventHandlers() {
        // Adblock prop
        Preference customPref = (Preference) findPreference(propIsAdEnabledKey);
        customPref.setOnPreferenceChangeListener(onTogglePreferenceListener);

        // Autoupdate prop
        Preference autoUpdatePref = (Preference) findPreference(propIsAutoupdateKey);
        autoUpdatePref.setOnPreferenceChangeListener(onTogglePreferenceListener);

        // Autoupdate prop
        Preference unstablePref = (Preference) findPreference(propAllowUnstable);
        unstablePref.setOnPreferenceChangeListener(onTogglePreferenceListener);

        // Update Now Button
        Preference updateNowPrefBtn = (Preference) findPreference(propUpdateNow);
        updateNowPrefBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (isNetworkAvailable()) {
                    checkForUpdates();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.avi_internet_required),
                            Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        ((Preference) findPreference("avi_author")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL));
                startActivity(browserIntent);
                return false;
            }
        });
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

    public void checkForUpdates() {
        final ProgressDialog preloader = new ProgressDialog(this);
        preloader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        preloader.setTitle(getResources().getString(R.string.upd_searching));
        preloader.setMessage(getResources().getString(R.string.avi_please_wait));
        preloader.setCancelable(false);
        preloader.show();

        boolean allowUnstable = preferences.getBoolean(propAllowUnstable, false);

        OTAUpdateChecker.checkForUpdates(new OTAStateListener() {
            @Override
            protected void onUpdateAvailable(AviSemVersion availableVersion, AviSemVersion currentVersion) {
                showUpdateDialog(availableVersion);
                preloader.hide();
            }

            @Override
            protected void onUpdateMissing(AviSemVersion availableVersion, AviSemVersion currentVersion) {
                preloader.hide();

                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.upd_not_found),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onError(Throwable t) {
                preloader.hide();

                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.upd_error),
                        Toast.LENGTH_SHORT).show();
            }
        }, allowUnstable);
    }

    private void startUpdate(AviSemVersion newVer) {
        startActivity(
                new Intent(this, UpdateDownloaderActivity.class)
                        .putExtra("update", newVer)
        );
    }

    private void showUpdateDialog(final AviSemVersion newVer) {
        OTAUpdateChecker.makeDialog(this, newVer)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startUpdate(newVer);
                        dialog.cancel();
                    }
                }).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
