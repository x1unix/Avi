package com.x1unix.avi;

import android.app.SearchManager;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.Log;
import java.util.List;

import com.x1unix.avi.rest.*;
import com.x1unix.avi.model.*;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.blabla);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView.setQueryHint(getResources().getString(R.string.avi_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener( ) {
            @Override
            public boolean   onQueryTextChange( String newText ) {
                // your text view here
                // textView.setText(newText);
                return false;
            }

            @Override
            public boolean   onQueryTextSubmit(String query) {
                textView.setText(query);
                KPApiInterface apiService =
                        KPRestClient.getClient().create(KPApiInterface.class);

                Call<KPMovieSearchResult> call = apiService.findMovies(query);
                call.enqueue(new Callback<KPMovieSearchResult>() {
                    @Override
                    public void onResponse(Call<KPMovieSearchResult>call, Response<KPMovieSearchResult> response) {
                        List<KPMovie> movies = response.body().getResults();
                        Log.d(TAG, "Number of movies received: " + movies.size());
                    }

                    @Override
                    public void onFailure(Call<KPMovieSearchResult>call, Throwable t) {
                        // Log error here since request failed
                        Log.e(TAG, t.toString());
                    }
                });
                return false;
            }
        });

        return true;
    }

    public boolean onQueryTextSubmit(String s){
        return false;
    }

    public boolean onQueryTextChange(String s) {
        return false;
    }

}
