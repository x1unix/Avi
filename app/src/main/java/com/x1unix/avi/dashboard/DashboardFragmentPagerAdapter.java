package com.x1unix.avi.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.x1unix.avi.R;
import com.x1unix.avi.storage.MoviesRepository;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragmentPagerAdapter extends FragmentPagerAdapter {
    private final int tabTitles[] = new int[] { R.string.favorites, R.string.viewed};
    private final List<DashboardTabFragment> fragments = new ArrayList<>();
    private Context context;
    private Resources res;

    public DashboardFragmentPagerAdapter(FragmentManager fm, Context context, MoviesRepository aMoviesRepository) {
        super(fm);
        this.context = context;
        this.res = context.getResources();

        // Add two test fragments
        this.fragments.add(FavoritesTabFragment.getInstance(aMoviesRepository));
        this.fragments.add(HistoryTabFragment.getInstance(aMoviesRepository));
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return res.getString(tabTitles[position]);
    }

    public void triggerUpdate() {
        for (DashboardTabFragment tab: fragments) {
            tab.updateLayout();
        }
    }

    public void triggerRescan() {
        for (DashboardTabFragment tab: fragments) {
            tab.rescanElements();
        }
    }
}