package com.x1unix.avi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.x1unix.avi.helpers.DownloadPosterTask;

public class MovieDetailsActivity extends AppCompatActivity {

    private Intent args;
    private String movieId;
    private String movieTitle;
    private String movieGenre;
    private String movieDescription;
    private String movieRating;
    private ActionBar actionBar;



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
}
