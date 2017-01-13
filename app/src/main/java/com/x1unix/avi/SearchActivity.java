package com.x1unix.avi;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.x1unix.avi.adapter.MoviesAdapter;
import com.x1unix.avi.model.KPMovieItem;
import com.x1unix.avi.model.KPMovieSearchResult;
import com.x1unix.avi.model.KPSearchResponse;
import com.x1unix.avi.rest.KPApiInterface;
import com.x1unix.avi.rest.KPRestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progress;
    private GridLayoutManager lLayout;
    private MenuItem searchItem;

    // Search results
    private List<KPMovieItem> movies = new ArrayList<KPMovieItem>();
    private RecyclerView searchResultsView;
    private KPApiInterface searchService = null;
    private ActionBar actionBar;
    private GridLayoutManager gridLayoutManager;

    private String query = "";

    private Button retryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        query = getIntent().getStringExtra("query");

        setContentView(R.layout.activity_search);

        initUI();

        if (query != null) {
            performSearch(query);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateSpanCount();
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_button:
                performSearch();
                break;
        }
    }

    private void initUI() {
        String title = "";
        if (query == null) {
            title = getResources().getString(R.string.avi_search_hint);
        } else {
            title = getResources().getString(R.string.search_for) + " \"" + query + "\"";
        }
        setTitle(title);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        // Set progress color
        progress.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.colorAccentDark),
                        PorterDuff.Mode.MULTIPLY);

        searchResultsView = (RecyclerView) findViewById(R.id.movies_recycler_view);

        initMoviesList();

        // Register RecyclerView event listener
        searchResultsView.addOnItemTouchListener(
                new RecyclerTouchListener(getApplicationContext(), searchResultsView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        openMovie(movies.get(position));
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initMoviesList() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int colsCount = (isTablet) ? getResources().getInteger(R.integer.colsCount) : 1;
        gridLayoutManager = new GridLayoutManager(this, colsCount);
        searchResultsView.setLayoutManager(gridLayoutManager);
    }

    private void updateSpanCount() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int colsCount = (isTablet) ? getResources().getInteger(R.integer.colsCount) : 1;
        gridLayoutManager.setSpanCount(colsCount);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Retrieve the SearchView and plug it into SearchManager
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
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
            public boolean onQueryTextSubmit(String newQuery) {
                if (newQuery.length() > 0) {
                    query = newQuery;
                    performSearch();
                }
                return false;
            }
        });

        if (query == null) {
            searchItem.expandActionView();
        }

        return true;
    }

    private void setProgressVisibility(boolean ifVisible) {
        progress.setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setErrorVisibility(boolean ifVisible) {
        // Add retry event Listener if not loaded
        if (ifVisible && (retryBtn == null)) {
            retryBtn = (Button) findViewById(R.id.retry_button);
            retryBtn.setOnClickListener(this);
        }

        ((LinearLayout) findViewById(R.id.error_message_screen))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setResultsVisibility(boolean ifVisible) {
        ((LinearLayout) findViewById(R.id.movies_results_screen))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setNoItemsVisibility(boolean ifVisible) {
        ((View) findViewById(R.id.nothing_found))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void drawResults() {
        MoviesAdapter adapter = new MoviesAdapter(movies,
                R.layout.list_item_movie,
                getApplicationContext(),
                getResources().getConfiguration().locale);

        if (adapter.getItemCount() > 0) {
            setResultsVisibility(true);
            searchResultsView.setAdapter(adapter);
        } else {
            setNoItemsVisibility(true);
        }
    }

    /**
     * Search results response handler
     */
    private Callback<KPSearchResponse> searchResultHandler = new Callback<KPSearchResponse>() {
        @Override
        public void onResponse(Call<KPSearchResponse> call, Response<KPSearchResponse> response) {
            setProgressVisibility(false);
            setNoItemsVisibility(false);
            setErrorVisibility(false);

            int statusCode = response.code();

            KPSearchResponse resp = response.body();

            if (resp != null) {
                KPMovieSearchResult result = resp.getData();

                if (result != null) {
                    movies = result.getResults();
                    drawResults();
                } else {
                    setNoItemsVisibility(true);
                }
            } else {
                setErrorVisibility(true);
            }
        }

        private void setErrorText(String txt) {
            ((TextView) findViewById(R.id.err_msg)).setText(txt);
        }

        @Override
        public void onFailure(Call<KPSearchResponse> call, Throwable t) {
            // Log error here since request failed
            setProgressVisibility(false);
            setResultsVisibility(false);
            setErrorVisibility(true);

            setErrorText(t.getMessage());
        }
    };

    /**
     * Perform search
     *
     * @param query {String} Search query
     */
    private void performSearch(String query) {
        this.query = query;

        setTitle(getResources().getString(R.string.search_for) + " \"" + query + "\"");

        if (searchService == null) {
            searchService = KPRestClient.getClient().create(KPApiInterface.class);
        }

        setResultsVisibility(false);
        setErrorVisibility(false);
        setNoItemsVisibility(false);
        setProgressVisibility(true);

        Call<KPSearchResponse> call = searchService.findMovies(query);
        call.enqueue(searchResultHandler);
    }

    private void performSearch() {
        performSearch(query);
    }

    /**
     * Open movie in player
     *
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

        startActivity(mIntent);
    }
}
