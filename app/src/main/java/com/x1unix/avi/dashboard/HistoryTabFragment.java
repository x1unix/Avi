package com.x1unix.avi.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.x1unix.avi.R;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.storage.MoviesRepository;

import java.util.ArrayList;

public class HistoryTabFragment extends DashboardTabFragment {

    @Override
    protected ArrayList<KPMovie> getContentItems() {
        return moviesRepository.getViewedMovies();
    }

    @Override
    protected int getTabView() {
        return R.layout.tab_history;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static DashboardTabFragment getInstance(MoviesRepository m) {
        return (new HistoryTabFragment()).setMoviesRepository(m);
    }
}