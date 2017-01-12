package com.x1unix.avi.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.x1unix.avi.R;
import com.x1unix.avi.storage.MoviesRepository;

public class HistoryTabFragment extends Fragment {
    private MoviesRepository moviesRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_history, container, false);
        // TextView textView = (TextView) view;
        // textView.setText("Fragment");
        return view;
    }

    public HistoryTabFragment setMoviesRepository(MoviesRepository m) {
        moviesRepository = m;
        return this;
    }

    public static HistoryTabFragment getInstance(MoviesRepository m) {
        return (new HistoryTabFragment()).setMoviesRepository(m);
    }
}