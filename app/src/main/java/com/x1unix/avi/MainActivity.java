package com.x1unix.avi;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.ConnectivityManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import android.net.NetworkInfo;

import com.x1unix.avi.rest.*;
import com.x1unix.avi.model.*;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.x1unix.avi.adapter.MoviesAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView moviesSearchResultsView;
    private KPApiInterface searchService = null;
    private MenuItem searchItem;
    private List<KPMovie> movies = new ArrayList<KPMovie>();

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
                    KPMovie movie = movies.get(position);
                    openMovie(movie);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } else {
            setStateVisibility(true, STATE_NO_INTERNET);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchItem = menu.findItem(R.id.action_search);

        prepareView();
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

        return true;
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
            setStateVisibility(true, STATE_LIST);

            int statusCode = response.code();
            Log.i(TAG, "Response received [" + String.valueOf(statusCode) + "]");
            movies = response.body().getResults();
            Log.i(TAG, "Items Length: " + String.valueOf(movies.size()));
            moviesSearchResultsView.setAdapter(new MoviesAdapter(movies, R.layout.list_item_movie, getApplicationContext()));
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
     * @param movie {KPMovie} movie instance
     */
    private void openMovie(KPMovie movie) {
        Intent mIntent = new Intent(this, MoviePlayerActivity.class);

        // Put id and title
        mIntent.putExtra("movieId", movie.getId());
        mIntent.putExtra("movieTitle", movie.getTitle());

        // Kickstart player
        Log.i("KPMovieOpen", "Trying to play movie [" + movie.getId() + "]");
        startActivity(mIntent);
    }


}
