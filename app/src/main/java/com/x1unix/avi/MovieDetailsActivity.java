package com.x1unix.avi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.x1unix.avi.adapter.MoviesAdapter;
import com.x1unix.avi.helpers.DownloadPosterTask;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.model.KPMovieSearchResult;
import com.x1unix.avi.model.KPPeople;
import com.x1unix.avi.rest.KPApiInterface;
import com.x1unix.avi.rest.KPRestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private Intent args;
    private String movieId;
    private String movieTitle;
    private String movieGenre;
    private String movieDescription;
    private String movieRating;
    private ActionBar actionBar;
    private KPApiInterface client;
    private String currentLocale;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        extractIntentData();
        setBasicMovieInfo();

        currentLocale = getResources().getConfiguration().locale.getLanguage();

        // Get movie info in background
        (new Thread(new Runnable() {
            @Override
            public void run() {
                getFullMovieInfo();
            }
        })).start();
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

        new DownloadPosterTask(
                (ImageView) findViewById(R.id.amd_movie_poster)
        ).getPosterByKpId(movieId);
    }

    private void setProgressVisibility(boolean ifShow) {
        int visible = (ifShow) ? View.VISIBLE : View.GONE;
        ((ProgressBar) findViewById(R.id.amd_preloader)).setVisibility(visible);
    }

    private void setInfoVisibility(boolean ifShow) {
        int visible = (ifShow) ? View.VISIBLE : View.GONE;
        ((LinearLayout) findViewById(R.id.amd_movie_info)).setVisibility(visible);
    }

    private void getFullMovieInfo() {
        client = KPRestClient.getClient().create(KPApiInterface.class);
        Call<KPMovie> call = client.getMovieById(movieId);
        call.enqueue(new Callback<KPMovie>() {
            @Override
            public void onResponse(Call<KPMovie>call, Response<KPMovie> response) {
                setProgressVisibility(false);

                int statusCode = response.code();
                KPMovie movie = response.body();

                if (movie != null) {
                    applyMovieData(movie);
                }
            }

            @Override
            public void onFailure(Call<KPMovie>call, Throwable t) {
                // Log error here since request failed
                setProgressVisibility(false);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_movie_info_fetch_fail) + t.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyMovieData(final KPMovie movie) {
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



        setInfoVisibility(true);
    }

    private void setAuthorInfo(List<KPPeople> src, int targetId) {
        String val = "-";
        if(!src.isEmpty()) {
            val = "";
            for (KPPeople man: src) {
                val += man.getName(currentLocale);
            }
        }
        ((TextView) findViewById(targetId)).setText(val);
    }
}
