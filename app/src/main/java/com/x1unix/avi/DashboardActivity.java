package com.x1unix.avi;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Bundle;
import android.view.Menu;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.net.NetworkInfo;

import com.x1unix.avi.rest.*;
import com.x1unix.avi.model.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.x1unix.avi.adapter.MoviesAdapter;
import com.x1unix.avi.updateManager.OTAStateListener;
import com.x1unix.avi.updateManager.OTAUpdateChecker;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    private RecyclerView moviesSearchResultsView;
    private KPApiInterface searchService = null;
    private MenuItem searchItem;
    private List<KPMovieItem> movies = new ArrayList<KPMovieItem>();

    // Menu items
    private MenuItem menuItemSettings;
    private MenuItem menuItemHelp;
    private Button connectionRefreshBtn;

    // Activity states
    private final int STATE_NO_INTERNET = 0;
    private final int STATE_WELCOME = 1;
    private final int STATE_ERROR = 2;
    private final int STATE_LIST = 3;

    // Views
    private ProgressBar progress;

    private LinearLayout[] states = {null, null, null, null};
    private int views[] = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Collect views
        progress = (ProgressBar) findViewById(R.id.progressBar);
        registerViews();

        // Set progress color
        progress.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccentDark), Mode.MULTIPLY);
        setProgressVisibility(true);

        moviesSearchResultsView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        moviesSearchResultsView.setLayoutManager(new LinearLayoutManager(this));

        // Set timer to try to check updates
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String keyPropAutoUpdate = getResources()
                        .getString(R.string.avi_prop_autocheck_updates);

                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());
                boolean allowAutoUpdateCheck = preferences.getBoolean(keyPropAutoUpdate, true);

                if (allowAutoUpdateCheck) {
                    tryFindUpdates();
                }
            }
        }, 1000);
    }

    private void registerViews() {
        views[0] = R.id.no_internet_screen;
        views[1] = R.id.wellcome_screen;
        views[2] = R.id.error_message_screen;
        views[3] = R.id.movies_results_screen;;
    }

    private LinearLayout getStateView(int stateId) {
        if (states[stateId] == null) {
            states[stateId] = (LinearLayout) findViewById(views[stateId]);
        }
        return states[stateId];
    }

    private void prepareView() {
        setProgressVisibility(false);
        boolean hasInet = isNetworkAvailable();

        setSearchVisibility(hasInet);

        if (isNetworkAvailable()) {
            setStateVisibility(true, STATE_WELCOME);

            // Register RecyclerView event listener
            moviesSearchResultsView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                    moviesSearchResultsView,
                    new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    KPMovieItem movie = movies.get(position);
                    openMovie(movie);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } else {
            setStateVisibility(true, STATE_NO_INTERNET);

            if (connectionRefreshBtn == null) {
                connectionRefreshBtn = (Button) findViewById(R.id.connection_refresh_button);
                connectionRefreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setStateVisibility(false, STATE_NO_INTERNET);
                        setProgressVisibility(true);
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchItem = menu.findItem(R.id.action_search);

        searchView.setQueryHint(getResources().getString(R.string.avi_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener( ) {
            @Override
            public boolean   onQueryTextChange( String newText ) {
                // your text view here
                // textView.setText(newText);
                return false;
            }

            @Override
            public boolean   onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }
        });

        prepareView();

        return true;
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

    private void setProgressVisibility(boolean ifVisible) {
        progress.setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setSearchVisibility(boolean ifVisible) {
        searchItem.setVisible(ifVisible);
    }

    private void setStateVisibility(boolean ifVisible, int stateId) {
        getStateView(stateId).setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }


    /**
     * Is network available
     * @return {boolean} Result
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Search results response handler
     */
    private Callback<KPMovieSearchResult> searchResultHandler = new Callback<KPMovieSearchResult>() {
        @Override
        public void onResponse(Call<KPMovieSearchResult>call, Response<KPMovieSearchResult> response) {
            setProgressVisibility(false);

            int statusCode = response.code();

            KPMovieSearchResult result = response.body();

            if (result != null) {
                movies = result.getResults();

                MoviesAdapter adapter = new MoviesAdapter(movies,
                        R.layout.list_item_movie,
                        getApplicationContext(),
                        getResources().getConfiguration().locale);

                if (adapter.getItemCount() > 0) {
                    setStateVisibility(true, STATE_LIST);
                    moviesSearchResultsView.setAdapter(adapter);
                } else {
                    setStateVisibility(false, STATE_LIST);
                    setStateVisibility(true, STATE_WELCOME);
                }
            } else {
                setStateVisibility(false, STATE_LIST);
                setStateVisibility(true, STATE_WELCOME);

                // Show message if there is no items
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.avi_no_items_msg), Toast.LENGTH_LONG)
                        .show();
            }
        }

        @Override
        public void onFailure(Call<KPMovieSearchResult>call, Throwable t) {
            // Log error here since request failed
            setProgressVisibility(false);
            setStateVisibility(true, STATE_ERROR);

            Log.e(TAG, "Failed to get items: " + t.toString());
            Toast.makeText(getApplicationContext(), "Failed to perform search: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Perform search
     * @param query {String} Search query
     */
    private void performSearch(String query) {
        if (searchService == null) {
            searchService = KPRestClient.getClient().create(KPApiInterface.class);
        }

        setStateVisibility(false, STATE_LIST);
        setStateVisibility(false, STATE_NO_INTERNET);
        setStateVisibility(false, STATE_ERROR);
        setStateVisibility(false, STATE_WELCOME);

        setProgressVisibility(true);
        Call<KPMovieSearchResult> call = searchService.findMovies(query);
        call.enqueue(searchResultHandler);
    }

    /**
     * Open movie in player
     * @param movie {KPMovieItem} movie instance
     */
    private void openMovie(KPMovieItem movie) {
        Intent mIntent = new Intent(this, MovieDetailsActivity.class);

        // Put id and title
        mIntent.putExtra("movieId", movie.getId());
        mIntent.putExtra("movieTitle", movie.getTitle());
        mIntent.putExtra("movieGenre", movie.getGenre());
        mIntent.putExtra("movieRating", movie.getRating());
        mIntent.putExtra("movieDescription", movie.getDescription());

        // Kickstart player
        Log.i("KPMovieOpen", "Trying to play movie [" + movie.getId() + "]");
        startActivity(mIntent);
    }

    private void tryFindUpdates() {
        if (isNetworkAvailable()) {
            OTAUpdateChecker.checkForUpdates(new OTAStateListener() {
                @Override
                protected void onUpdateAvailable(AviSemVersion availableVersion, AviSemVersion currentVersion) {
                    showUpdateDialog(availableVersion);
                }
            });
        }
    }

    private void showUpdateDialog(final AviSemVersion newVer) {
        AlertDialog.Builder dialInstallUpdate = new AlertDialog.Builder(this);
        String modConfimText = getResources().getString(R.string.upd_confirm);
        modConfimText = modConfimText.replace("@version", newVer.toString());

        dialInstallUpdate.setMessage(modConfimText);
        dialInstallUpdate.setTitle(getResources().getString(R.string.upd_new_available))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newVer.getApkUrl()));
                        startActivity(browserIntent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialInstallUpdate.show();
    }


}
