package com.x1unix.avi;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.x1unix.avi.dashboard.*;
import com.x1unix.avi.storage.MoviesRepository;

public class NewDashboardActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private MenuItem searchItem;

    // Menu items
    private MenuItem menuItemSettings;
    private MenuItem menuItemHelp;
    private MoviesRepository moviesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        moviesRepository = MoviesRepository.getInstance(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new DashboardFragmentPagerAdapter(getSupportFragmentManager(),
                NewDashboardActivity.this, moviesRepository));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);


        // Import menu items
        menuItemSettings = (MenuItem) menu.findItem(R.id.menu_action_settings);
        menuItemHelp = (MenuItem) menu.findItem(R.id.menu_action_help);
        registerMenuItemsClickListeners();

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchItem = menu.findItem(R.id.action_search);

        searchView.setQueryHint(getResources().getString(R.string.avi_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // your text view here
                // textView.setText(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }
        });

        return true;
    }

    private void performSearch(String query) {
        searchItem.collapseActionView();
        startActivity(
                (new Intent(this, SearchActivity.class)).putExtra("query", query)
        );
    }

    /**
     * Load event handlers for menu buttons in paralel thread
     */
    private void registerMenuItemsClickListeners() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                menuItemSettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent i = new Intent(getBaseContext(), SettingsActivity.class);
                        startActivity(i);
                        return false;
                    }
                });

                menuItemHelp.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent i = new Intent(getBaseContext(), SupportActivity.class);
                        startActivity(i);
                        return false;
                    }
                });
            }
        }, 100);
    }
}
