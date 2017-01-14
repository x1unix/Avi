package com.x1unix.avi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kinopoisk.Constants;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.model.KPMovieDetailViewResponse;
import com.x1unix.avi.model.KPPeople;
import com.x1unix.avi.rest.KPApiInterface;
import com.x1unix.avi.rest.KPRestClient;
import com.x1unix.avi.storage.MoviesRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent args;
    private String movieId;
    private String movieTitle;
    private String movieGenre;
    private String movieDescription;
    private String movieRating;
    private ActionBar actionBar;
    private KPApiInterface client;
    private String currentLocale;

    private ImageButton btnAddToBookmarks;
    private ImageButton btnRmFromBookmarks;

    private Button btnWatchMovie;
    private Button btnRetry;
    private MoviesRepository moviesRepository;
    private String LOG_TAG = "MovieDetailsActivity";
    private boolean isCached = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Init UI elements
        initUIElements();

        moviesRepository = MoviesRepository.getInstance(this);

        extractIntentData();
        setBasicMovieInfo();

        currentLocale = getResources().getConfiguration().locale.getLanguage();

        // Get movie info
        if (moviesRepository.movieExists(movieId)) {
            Log.d(LOG_TAG, "#" + movieId + " Cached!");

            // Import data from cache
            KPMovie movie = moviesRepository.getMovieById(movieId);
            applyMovieData(movie, false);
            isCached = true;
        } else {
            Log.d(LOG_TAG, "#" + movieId +" No data in cache");
            getFullMovieInfo();
        }

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

    private void initUIElements() {
        btnAddToBookmarks = (ImageButton) findViewById(R.id.amd_bookmark_add);
        btnRmFromBookmarks = (ImageButton) findViewById(R.id.amd_bookmark_remove);
        btnWatchMovie = (Button) findViewById(R.id.amd_btn_watch);
        btnRetry = (Button) findViewById(R.id.amd_retry);

        btnWatchMovie.setOnClickListener(this);
        btnAddToBookmarks.setOnClickListener(this);
        btnRmFromBookmarks.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
    }

    @Override
    public void onStop() {
        moviesRepository.close();
        moviesRepository = null;
        super.onStop();
    }

    @Override
    public void onRestart() {
        moviesRepository = MoviesRepository.getInstance(this);
        super.onRestart();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.amd_btn_watch:
                watchMovie();
                break;
            case R.id.amd_retry:
                retry();
                break;
            case R.id.amd_bookmark_add:
                btnAddToBookmarks.setVisibility(View.GONE);
                moviesRepository.addToFavorites(movieId);
                setBookmarkedVisibility(true);
                showSnackMessage(R.string.added_to_favs);
                break;
            case R.id.amd_bookmark_remove:
                btnRmFromBookmarks.setVisibility(View.GONE);
                moviesRepository.removeFromFavorites(movieId);
                setBookmarkedVisibility(false);
                showSnackMessage(R.string.removed_from_favs);
                break;
            default:
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.feature_not_available),
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void watchMovie() {
        Intent wmIntent = new Intent(this, MoviePlayerActivity.class);

        // Put id and title
        wmIntent.putExtra("movieId", movieId);
        wmIntent.putExtra("movieTitle", movieTitle);

        // Kickstart player
        startActivity(wmIntent);
    }

    private void extractIntentData() {
        args = getIntent();

        movieId = args.getStringExtra("movieId");
        movieTitle = args.getStringExtra("movieTitle");
        movieGenre = args.getStringExtra("movieGenre");
        movieDescription = args.getStringExtra("movieDescription");
        movieRating = args.getStringExtra("movieRating");
    }

    private void setBasicMovieInfo() {
        ((TextView) findViewById(R.id.amd_movie_title)).setText(movieTitle);
        ((TextView) findViewById(R.id.amd_short_desc)).setText(movieDescription);
        ((TextView) findViewById(R.id.amd_rating)).setText(movieRating);

        setTitle(movieTitle);

        Glide.with(getApplicationContext())
                .load(Constants.getPosterUrl(movieId))
                .placeholder(R.drawable.no_poster)
                .into((ImageView) findViewById(R.id.amd_movie_poster));
    }

    private void setProgressVisibility(boolean ifShow) {
        int visible = (ifShow) ? View.VISIBLE : View.GONE;
        ((ProgressBar) findViewById(R.id.amd_preloader)).setVisibility(visible);
    }

    private void setInfoVisibility(boolean ifShow) {
        int visible = (ifShow) ? View.VISIBLE : View.GONE;
        ((LinearLayout) findViewById(R.id.amd_movie_info)).setVisibility(visible);
    }

    private void setErrVisibility(boolean ifShow) {
        int visible = (ifShow) ? View.VISIBLE : View.GONE;
        ((LinearLayout) findViewById(R.id.amd_msg_fail)).setVisibility(visible);
    }

    private void setBookmarkedVisibility(boolean ifShow) {
        btnRmFromBookmarks.setVisibility((ifShow) ? View.VISIBLE : View.GONE);
        btnAddToBookmarks.setVisibility((!ifShow) ? View.VISIBLE : View.GONE);
    }



    private void getFullMovieInfo() {
        if (client == null) {
            client = KPRestClient.getClient().create(KPApiInterface.class);
        }
        Call<KPMovieDetailViewResponse> call = client.getMovieById(movieId);
        call.enqueue(new Callback<KPMovieDetailViewResponse>() {
            @Override
            public void onResponse(Call<KPMovieDetailViewResponse>call, Response<KPMovieDetailViewResponse> response) {
                int statusCode = response.code();
                KPMovieDetailViewResponse result = response.body();

                if (result != null) {
                    KPMovie movie = result.getResult();
                    if (movie == null) {
                        showNoMovieDataMsg(result.getMessage());
                    } else {
                        applyMovieData(movie);
                    }
                }
            }

            @Override
            public void onFailure(Call<KPMovieDetailViewResponse>call, Throwable t) {
                showNoMovieDataMsg(t.toString());
            }
        });
    }

    private void retry() {
        setErrVisibility(false);
        setProgressVisibility(true);
        getFullMovieInfo();
    }

    private void showNoMovieDataMsg(String msg) {
        // Log error here since request failed
        setProgressVisibility(false);
        setErrVisibility(true);

        String prefix = getResources().getString(R.string.err_movie_info_fetch_fail);
        Toast.makeText(getApplicationContext(), prefix + " " + msg,
                Toast.LENGTH_LONG).show();
    }

    private void applyMovieData(final KPMovie movie, boolean cacheItem) {
        setProgressVisibility(false);

        // Main Info
        ((TextView) findViewById(R.id.amd_description)).setText(movie.getDescription());
        ((TextView) findViewById(R.id.amd_genre)).setText(movie.getGenre());
        ((TextView) findViewById(R.id.amd_age_restrictions)).setText(movie.getRatingMPAA());
        ((TextView) findViewById(R.id.amd_year)).setText(movie.getYear());
        ((TextView) findViewById(R.id.amd_length)).setText(movie.getFilmLength());

        // Artists, etc.
        setAuthorInfo(movie.getDirectors(), R.id.amd_directors);
        setAuthorInfo(movie.getActors(), R.id.amd_actors);
        setAuthorInfo(movie.getProducers(), R.id.amd_producers);


        // Save movie to the cache
        if (cacheItem) {
            moviesRepository.addMovie(
                    movie.setShortDescription(movieDescription).setStars(movieRating)
            );
        }

        setBookmarkedVisibility(moviesRepository.isInFavorites(movieId));
        setInfoVisibility(true);
    }

    private void applyMovieData(final KPMovie movie) {
        applyMovieData(movie, true);
    }

    private void setAuthorInfo(KPPeople[] src, int targetId) {
        String val = "-";
        if(src.length > 0) {
            val = "";
            for(int c = 0; c < src.length; c++) {
                String name = src[c].getName(currentLocale);
                if (name != null) {
                    val += src[c].getName(currentLocale);
                    if (c < (src.length - 1)) {
                        val += ", ";
                    }
                }
            }
        }
        ((TextView) findViewById(targetId)).setText(val);
    }

    private void showSnackMessage(int stringId) {
        Snackbar.make(findViewById(android.R.id.content),
                    getResources().getString(stringId),
                    Snackbar.LENGTH_LONG).show();
    }
}
