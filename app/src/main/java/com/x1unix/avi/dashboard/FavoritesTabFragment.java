package com.x1unix.avi.dashboard;

import com.x1unix.avi.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.storage.MoviesRepository;
import java.util.ArrayList;

public class FavoritesTabFragment extends DashboardTabFragment {

    @Override
    protected ArrayList<KPMovie> getContentItems() {
        return moviesRepository.getFavoritesMovies();
    }

    @Override
    protected int getTabView() {
        return R.layout.tab_favorites;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getPlaylistGenitivusName() {
        return getResources().getString(R.string.playlist_genitivus_favorites);
    }

    @Override
    protected boolean onItemRemoveRequest(KPMovie item) {
        moviesRepository.removeFromFavorites(item.getId());
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static DashboardTabFragment getInstance(MoviesRepository m) {
        return (new FavoritesTabFragment()).setMoviesRepository(m);
    }
}