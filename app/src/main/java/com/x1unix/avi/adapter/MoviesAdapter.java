package com.x1unix.avi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import com.x1unix.avi.R;
import com.x1unix.avi.helpers.DownloadPosterTask;
import com.x1unix.avi.model.KPMovieItem;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<KPMovieItem> movies;
    private int rowLayout;
    private Context context;
    private String currentLang = "ru";


    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        LinearLayout moviesLayout;
        TextView movieTitle;
        TextView data;
        TextView movieDescription;
        TextView rating;
        ImageView posterView;
        String kpId;


        public MovieViewHolder(View v) {
            super(v);
            moviesLayout = (LinearLayout) v.findViewById(R.id.movies_layout);
            movieTitle = (TextView) v.findViewById(R.id.title);
            data = (TextView) v.findViewById(R.id.subtitle);
            movieDescription = (TextView) v.findViewById(R.id.description);
            rating = (TextView) v.findViewById(R.id.rating);
            posterView = (ImageView) v.findViewById(R.id.poster_preview);
        }

        public void loadPoster() {
            new DownloadPosterTask(posterView).getPosterByKpId(kpId);
        }
    }

    public MoviesAdapter(List<KPMovieItem> movies, int rowLayout, Context context, Locale currentLocale) {
        this.movies = movies;
        this.rowLayout = rowLayout;
        this.context = context;
        this.currentLang = currentLocale.getLanguage();

        // Show toast message if no items
        if (getItemCount() == 0) {
            Toast noItemsMsg = Toast.makeText(
                    context,
                    context.getResources().getString(R.string.avi_no_items_msg),
                    Toast.LENGTH_LONG);
            noItemsMsg.show();
        }
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new MovieViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, final int position) {
        KPMovieItem cMovie = movies.get(position);
        holder.movieTitle.setText(cMovie.getLocalizedTitle(currentLang));
        holder.data.setText(cMovie.getReleaseDate());
        holder.movieDescription.setText(cMovie.getDescription());
        holder.rating.setText(String.valueOf(cMovie.getVoteAverage()));
        holder.kpId = cMovie.getId();
        holder.loadPoster();
    }

    @Override
    public int getItemCount() {
        return (movies == null) ? 0 : movies.size();
    }
}