package com.x1unix.avi;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class SearchActivity extends AppCompatActivity {

    private ProgressBar progress;
    private GridLayoutManager lLayout;

    // Search results
    private List<KPMovieItem> movies = new ArrayList<KPMovieItem>();
    private RecyclerView moviesSearchResultsView;
    private KPApiInterface searchService = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        // Set progress color
        progress.getIndeterminateDrawable()
                .setColorFilter(getResources().getColor(R.color.colorAccentDark),
                        PorterDuff.Mode.MULTIPLY);

        setContentView(R.layout.activity_search);
    }

    private void setProgressVisibility(boolean ifVisible) {
        progress.setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setErrorVisibility(boolean ifVisible) {
        ((LinearLayout) findViewById(R.id.error_message_screen))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    private void setResultsVisibility(boolean ifVisible) {
        ((LinearLayout) findViewById(R.id.movies_results_screen))
                .setVisibility(ifVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * Search results response handler
     */
    private Callback<KPSearchResponse> searchResultHandler = new Callback<KPSearchResponse>() {
        @Override
        public void onResponse(Call<KPSearchResponse> call, Response<KPSearchResponse> response) {
            setProgressVisibility(false);
            setErrorVisibility(false);

            int statusCode = response.code();

            KPSearchResponse resp = response.body();

            if (resp != null) {
                KPMovieSearchResult result = resp.getData();

                if (result != null) {
                    movies = result.getResults();

                    MoviesAdapter adapter = new MoviesAdapter(movies,
                            R.layout.list_item_movie,
                            getApplicationContext(),
                            getResources().getConfiguration().locale);

                    if (adapter.getItemCount() > 0) {
                        setResultsVisibility(true);
                        moviesSearchResultsView.setAdapter(adapter);
                    } else {
                        setErrorVisibility(true);
                    }
                } else {
                    showNoItems();
                }
            } else {
                showNoItems();
            }
        }

        private void showNoItems() {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.avi_no_items_msg), Toast.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onFailure(Call<KPSearchResponse>call, Throwable t) {
            // Log error here since request failed
            setProgressVisibility(false);
            setResultsVisibility(false);
            setErrorVisibility(true);

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

        setResultsVisibility(false);
        setErrorVisibility(false);

        setProgressVisibility(true);
        Call<KPSearchResponse> call = searchService.findMovies(query);
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

        startActivity(mIntent);
    }
}
