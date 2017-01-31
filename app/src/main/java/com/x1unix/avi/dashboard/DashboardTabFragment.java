package com.x1unix.avi.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.x1unix.avi.ClickListener;
import com.x1unix.avi.MovieDetailsActivity;
import com.x1unix.avi.MoviePlayerActivity;
import com.x1unix.avi.R;
import com.x1unix.avi.RecyclerTouchListener;
import com.x1unix.avi.adapter.CachedMoviesListAdapter;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.storage.MoviesRepository;
import java.util.ArrayList;

public class DashboardTabFragment extends Fragment {
    protected MoviesRepository moviesRepository;
    protected View noItemsView;
    protected ArrayList<KPMovie> items;
    protected RecyclerView itemsListView;
    protected String currentLang = "ru";
    protected GridLayoutManager gridLayoutManager;
    protected CachedMoviesListAdapter moviesListAdapter;
    private SwipeRefreshLayout swipeContainer;
    private AlertDialog.Builder dialog;

    private String[] popupMenuItems;
    private Resources res;

    // Android recommends to avoid enums, so use regular const
    private static final int OPTION_WATCH = 0;
    private static final int OPTION_INFORMATION = 1;
    private static final int OPTION_REMOVE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        res = getResources();

        if (moviesRepository != null) {
            items = getContentItems();
        }

        currentLang = res.getConfiguration().locale.getLanguage();

        // Define pop-up menu items
        popupMenuItems = new String[] {
                res.getString(R.string.open_in_player),
                res.getString(R.string.show_details),
                res.getString(R.string.remove_from)
                        .replace("%PLAYLIST%", getPlaylistGenitivusName())
        };
    }

    protected void onLayoutUpdate() {
        updateSpanCount();
    }

    /**
     * Return resource ID of this tab view from R.layout
     * @return id
     */
    protected int getTabView() {
        return 0;
    }

    /**
     * Provide items for playlist tab
     * @return movies collection
     */
    protected ArrayList<KPMovie> getContentItems() {
        return null;
    }


    /**
     * Event handler that makes database query and returns if movie can be removed from array
     * @param item movie
     * @return result
     */
    protected boolean onItemRemoveRequest(KPMovie item) {
        return true;
    }

    /**
     * Current playlist name for pop-up dialogue
     * @return playlist name
     */
    protected String getPlaylistGenitivusName() {
        return res.getString(R.string.playlist_genitivus_default);
    }

    /**
     * Long tap event listener
     * @param item selected movie
     * @param v Current view
     * @param position store array position
     */
    protected void onItemLongPress(final KPMovie item, final View v, final int position) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(getActivity());
        }

        dialog.setTitle(item.getLocalizedTitle(currentLang))
                .setItems(popupMenuItems, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case OPTION_INFORMATION:
                                openMovie(item);
                                break;
                            case OPTION_WATCH:
                                watchMovie(item);
                                break;
                            case OPTION_REMOVE:
                                removeItemBlock(item, position);
                                break;
                            default:
                                break;
                        }
                    }
                }
        ).show();
    }

    private void removeItemBlock(KPMovie movie, int index) {
        if (onItemRemoveRequest(movie)) {
            items.remove(index);
            rescanElements(false);
        }
    }

    private void watchMovie(KPMovie movie) {
        Intent wmIntent = new Intent(getActivity(), MoviePlayerActivity.class);

        // Put id and title
        wmIntent.putExtra("movieId", movie.getId());
        wmIntent.putExtra("movieTitle", movie.getLocalizedTitle(currentLang));

        // Fire UP player
        startActivity(wmIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getTabView(), container, false);

        noItemsView = view.findViewById(R.id.no_items_block);
        itemsListView = (RecyclerView) view.findViewById(R.id.items_recycler_view);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.movies_swipe_container);

        swipeContainer.setColorSchemeResources(R.color.colorAccentDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_light);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rescanElements();
                swipeContainer.setRefreshing(false);
            }
        });


        initRecycleView();
        renderMovies();

        return view;
    }


    protected void renderMovies() {
        // Fix #61 java.lang.NullPointerException
        boolean hasItems = (items != null) && (items.size() > 0);

        noItemsView.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        itemsListView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        moviesListAdapter = new CachedMoviesListAdapter(items,
                R.layout.list_item_movie,
                getContext(),
                getResources().getConfiguration().locale);

        itemsListView.setAdapter(moviesListAdapter);

    }

    protected void configureRecycleView() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int colsCount = (isTablet) ? getResources().getInteger(R.integer.colsCount) : 1;

        gridLayoutManager = new GridLayoutManager(getContext(), colsCount);
        itemsListView.setLayoutManager(gridLayoutManager);
    }

    protected void updateSpanCount() {
        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
        int colsCount = (isTablet) ? getResources().getInteger(R.integer.colsCount) : 1;
        gridLayoutManager.setSpanCount(colsCount);
    }

    protected void initRecycleView() {
        configureRecycleView();

        // Register RecyclerView event listener
        itemsListView.addOnItemTouchListener(
                new RecyclerTouchListener(getContext(), itemsListView, new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        openMovie(items.get(position));
                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        onItemLongPress(items.get(position), view, position);
                    }
                }));
    }

    protected void openMovie(KPMovie movie) {
        Intent mIntent = new Intent(getContext(), MovieDetailsActivity.class);

        // Put id and title
        mIntent.putExtra("movieId", movie.getId());
        mIntent.putExtra("movieTitle", movie.getLocalizedTitle(currentLang));
        mIntent.putExtra("movieGenre", movie.getGenre());
        mIntent.putExtra("movieRating", movie.getStars());
        mIntent.putExtra("movieDescription", movie.getShortDescription());

        startActivity(mIntent);
    }

    protected DashboardTabFragment setMoviesRepository(MoviesRepository m) {
        moviesRepository = m;
        return this;
    }

    public static DashboardTabFragment getInstance(MoviesRepository m, int iviewId) {
        return (new DashboardTabFragment())
                    .setMoviesRepository(m);
    }

    public void rescanElements(boolean updateStorage) {
        if ((moviesRepository != null) && updateStorage) {
            items = getContentItems();
        }

        if (moviesListAdapter != null) {
            moviesListAdapter.notifyDataSetChanged();
        }

        renderMovies();
    }

    public void rescanElements() {
        rescanElements(true);
    }

    public void updateLayout() {
        onLayoutUpdate();
    }
}
