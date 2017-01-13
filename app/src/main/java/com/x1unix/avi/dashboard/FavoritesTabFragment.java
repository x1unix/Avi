package com.x1unix.avi.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.x1unix.avi.ClickListener;
import com.x1unix.avi.MovieDetailsActivity;
import com.x1unix.avi.R;
import com.x1unix.avi.RecyclerTouchListener;
import com.x1unix.avi.adapter.CachedMoviesListAdapter;
import com.x1unix.avi.adapter.MoviesAdapter;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.model.KPMovieItem;
import com.x1unix.avi.storage.MoviesRepository;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;

public class FavoritesTabFragment extends Fragment {
    private MoviesRepository moviesRepository;
    private ArrayList<KPMovie> items;
    private View noItemsView;
    private RecyclerView itemsListView;
    private String currentLang = "ru";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (moviesRepository != null) {
            items = moviesRepository.getFavoritesMovies();
        }

        currentLang = getResources().getConfiguration().locale.getLanguage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_favorites, container, false);

        noItemsView = view.findViewById(R.id.no_items_block);
        itemsListView = (RecyclerView) view.findViewById(R.id.favs_recycler_view);

        initRecycleView();
        renderMovies();

        return view;
    }

    private void renderMovies() {
        boolean hasFavorites = (items.size() > 0);

        noItemsView.setVisibility(hasFavorites ? View.GONE : View.VISIBLE);
        itemsListView.setVisibility(hasFavorites ? View.VISIBLE : View.GONE);

        itemsListView.setAdapter(new CachedMoviesListAdapter(items,
                R.layout.list_item_movie,
                getContext(),
                getResources().getConfiguration().locale));

    }

    private void initRecycleView() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int colsCount = (isTablet) ? getResources().getInteger(R.integer.colsCount) : 1;
        itemsListView.setLayoutManager(new GridLayoutManager(getContext(), colsCount));

        // Register RecyclerView event listener
        itemsListView.addOnItemTouchListener(
                new RecyclerTouchListener(getContext(), itemsListView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        openMovie(items.get(position));
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
    }

    private void openMovie(KPMovie movie) {
        Intent mIntent = new Intent(getContext(), MovieDetailsActivity.class);

        // Put id and title
        mIntent.putExtra("movieId", movie.getId());
        mIntent.putExtra("movieTitle", movie.getLocalizedTitle(currentLang));
        mIntent.putExtra("movieGenre", movie.getGenre());
        mIntent.putExtra("movieRating", movie.getStars());
        mIntent.putExtra("movieDescription", movie.getShortDescription());

        startActivity(mIntent);
    }

    public FavoritesTabFragment setMoviesRepository(MoviesRepository m) {
        moviesRepository = m;
        return this;
    }

    public static FavoritesTabFragment getInstance(MoviesRepository m) {
        return (new FavoritesTabFragment()).setMoviesRepository(m);
    }
}