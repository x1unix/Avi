package com.x1unix.avi;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import com.x1unix.avi.BuildConfig;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.util.Log;
import android.net.NetworkInfo;
import com.x1unix.avi.model.*;

import com.x1unix.avi.updateManager.OTAStateListener;
import com.x1unix.avi.updateManager.OTAUpdateChecker;

public class DashboardActivity extends AppCompatActivity {

    private MenuItem searchItem;

    // Menu items
    private MenuItem menuItemSettings;
    private MenuItem menuItemHelp;
    private Button connectionRefreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set timer to try to check updates
        if (!BuildConfig.DEBUG) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String keyPropAutoUpdate = getResources()
                            .getString(R.string.avi_prop_autocheck_updates);

                    String keyAllowUnstable = getResources()
                            .getString(R.string.avi_prop_allow_unstable);

                    SharedPreferences preferences = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    boolean allowAutoUpdateCheck = preferences.getBoolean(keyPropAutoUpdate, true);
                    boolean allowUnstable = preferences.getBoolean(keyAllowUnstable, false);

                    if (allowAutoUpdateCheck) {
                        tryFindUpdates(allowUnstable);
                    }
                }
            }, 1000);
        }
    }

    private void setNoInternetVisibility(boolean ifVisible) {
        ((LinearLayout) findViewById(R.id.no_internet_screen))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setSearchVisibility(boolean ifVisible) {
        searchItem.setVisible(ifVisible);
    }

    private void setWellcomeVisibility(boolean ifVisible) {
        ((LinearLayout) findViewById(R.id.wellcome_screen))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void prepareView() {
        boolean hasInet = isNetworkAvailable();

        // For test purposes
        if (BuildConfig.DEBUG) {
            Log.i("APP", "Debug mode ENABLED");
            ((ImageView) findViewById(R.id.testBtn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent wmIntent = new Intent(getApplicationContext(), MovieDetailsActivity.class);

                    // Put id and title
                    wmIntent.putExtra("movieId", "770");
                    wmIntent.putExtra("movieTitle", "Ocean's Eleven");
                    wmIntent.putExtra("movieGenre", "Test");
                    wmIntent.putExtra("movieDescription", "Test");
                    wmIntent.putExtra("movieRating", "5.0");

                    // Kickstart player
                    startActivity(wmIntent);
                }
            });
        }


        if (isNetworkAvailable()) {
            setWellcomeVisibility(true);
            setNoInternetVisibility(false);
            setSearchVisibility(true);

        } else {
            setWellcomeVisibility(false);
            setSearchVisibility(false);
            setNoInternetVisibility(true);

            if (connectionRefreshBtn == null) {
                connectionRefreshBtn = (Button) findViewById(R.id.connection_refresh_button);
                connectionRefreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prepareView();
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


        // Import menu items
        menuItemSettings = (MenuItem) menu.findItem(R.id.menu_action_settings);
        menuItemHelp = (MenuItem) menu.findItem(R.id.menu_action_help);
        registerMenuItemsClickListeners();

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchItem = menu.findItem(R.id.action_search);

        searchView.setQueryHint(getResources().getString(R.string.avi_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                // textView.setText(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }
        });

        prepareView();

        return true;
    }

    private void performSearch(String query) {
        searchItem.collapseActionView();
        startActivity(
                (new Intent(this, SearchActivity.class)).putExtra("query", query)
        );
    }

    /**
     * Load event handlers for menu buttons in paralel thread
     */
    private void registerMenuItemsClickListeners() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                menuItemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(i);
                        return false;
                    }
                });

                menuItemHelp.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent i = new Intent(getBaseContext(), SupportActivity.class);
                        startActivity(i);
                        return false;
                    }
                });
            }
        }, 100);
    }


    /**
     * Is network available
     *
     * @return {boolean} Result
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void tryFindUpdates(boolean allowUnstable) {
        if (isNetworkAvailable()) {
            OTAUpdateChecker.checkForUpdates(new OTAStateListener() {
                @Override
                protected void onUpdateAvailable(AviSemVersion availableVersion, AviSemVersion currentVersion) {
                    showUpdateDialog(availableVersion);
                }
            }, allowUnstable);
        }
    }


    private void showUpdateDialog(final AviSemVersion newVer) {
        OTAUpdateChecker.makeDialog(this, newVer).show();
    }


}
