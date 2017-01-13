package com.x1unix.avi.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.x1unix.avi.model.KPMovie;
import com.x1unix.avi.model.KPPeople;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {

    public static final String TABLE_MOVIES = "movies";
    public static final String TABLE_FAVORITES = "favorites";
    public static final String TABLE_VIEWED = "viewed";

    private static final String LOG_TAG = "MoviesRepository";
    public static final Type MOVIE_TYPE = new TypeToken<List<KPPeople[]>>() {
    }.getType();

    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Boolean connected = false;

    public MoviesRepository(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
        connect();
    }

    public MoviesRepository addMovie(KPMovie movie) {
        ContentValues cv = new ContentValues();

        // Write basic fields
        cv.put("filmID", movie.getId());
        cv.put("nameRU", movie.getNameRU());
        cv.put("nameEN", movie.getNameEN());
        cv.put("year", movie.getYear());
        cv.put("filmLength", movie.getFilmLength());
        cv.put("country", movie.getCountry());
        cv.put("genre", movie.getGenre());
        cv.put("description", movie.getDescription());
        cv.put("ratingMPAA", movie.getTrueRatingMPAA());
        cv.put("ratingAgeLimits", movie.getRatingAgeLimits());
        cv.put("type", movie.getType());
        cv.put("shortDescription", movie.getShortDescription());
        cv.put("stars", movie.getStars());

        // Serialize data to JSON and save as BLOB
        cv.put("creators", (new Gson()).toJson(movie.getCreators()));

        // Write movie to db
        long rowID = db.insert(MoviesRepository.TABLE_MOVIES, null, cv);
        Log.d(MoviesRepository.LOG_TAG, "Movie row inserted, ID: " + rowID);

        return this;
    }

    public boolean movieExists(String kpId) {
        String query = "SELECT * FROM " + MoviesRepository.TABLE_MOVIES + " where filmID = " + kpId + ";";
        Cursor cursor = db.rawQuery(query, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();

        return exists;
    }

    public boolean movieHistoryExists(String kpId) {
        String query = "SELECT * FROM " + MoviesRepository.TABLE_VIEWED + " where filmID = " + kpId + ";";
        Cursor cursor = db.rawQuery(query, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();

        return exists;
    }

    public void addItemToHistory(String kpId) {
        ContentValues cv = new ContentValues();
        cv.put("filmID", kpId);
        cv.put("lastViewed", getCurrentTimeStamp());

        long rowID = db.insert(MoviesRepository.TABLE_VIEWED, null, cv);
        Log.d(MoviesRepository.LOG_TAG, "Movie added to history #" + kpId);
    }

    public void updateItemHistory(String kpId) {
        ContentValues cv = new ContentValues();
        cv.put("filmID", kpId);
        cv.put("lastViewed", getCurrentTimeStamp());

        db.update(MoviesRepository.TABLE_VIEWED, cv, "filmID="+kpId, null);
    }

    private long getCurrentTimeStamp() {
        return System.currentTimeMillis()/1000;
    }

    private String getSelect() {
        return "SELECT * FROM " + MoviesRepository.TABLE_MOVIES;
    }

    public KPMovie getMovieById(String filmId) {
        KPMovie movie = null;
        String query = getSelect() + " where filmID = " + filmId + ";";
        Cursor c = db.rawQuery(query, null);

        try {
            if ((c != null) && c.moveToFirst()) {
                movie = MoviesRepository.getMovieFromCursor(c);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to get movie from cursor, query: \n" + query + "\n Message: " + e.getMessage());
            movie = null;
        }

        if (c != null) c.close();

        return movie;
    }

    public boolean isInFavorites(String kpId) {
        String query = "SELECT * FROM " + MoviesRepository.TABLE_FAVORITES + " where filmID = " + kpId + ";";
        Cursor cursor = db.rawQuery(query, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();

        return exists;
    }

    public boolean removeFromFavorites(String kpId) {
        return db.delete(MoviesRepository.TABLE_FAVORITES, "filmID = " + kpId, null) > 0;
    }

    public void addToFavorites(String kpId) {
        ContentValues cv = new ContentValues();
        cv.put("filmID", kpId);

        long rowID = db.insert(MoviesRepository.TABLE_FAVORITES, null, cv);
        Log.d(MoviesRepository.LOG_TAG, "Add to favorites, ID: " + rowID);
    }

    private ArrayList<KPMovie> getMoviesFromPlaylist(String playlistName, String extraQuery) {
        ArrayList<KPMovie> result = new ArrayList<KPMovie>();

        if (extraQuery == null) extraQuery = "";

        String q =  "select m.*\n" +
                "    from movies m\n" +
                "    join " + playlistName + " f\n" +
                "    on m.filmID = f.filmID " + extraQuery + ";";

        Cursor c = db.rawQuery(q, null);

        if (c.moveToFirst()) {
            do {
                result.add(MoviesRepository.getMovieFromCursor(c));
            } while(c.moveToNext());
        }

        if (c != null && !c.isClosed()){
            c.close();
        }

        return result;
    }

    public ArrayList<KPMovie> getFavoritesMovies() {
        return getMoviesFromPlaylist(TABLE_FAVORITES, null);
    }

    public ArrayList<KPMovie> getViewedMovies() {
        return getMoviesFromPlaylist(TABLE_VIEWED, "order by f.lastViewed DESC limit 80");
    }

    public void close() {
        connected = false;
        db.close();
    }

    public void connect() {
        db = dbHelper.getWritableDatabase();
        connected = true;
    }

    private static String getStringValue(String value, Cursor c) {
        return c.getString(c.getColumnIndex(value));
    }

    public static KPMovie getMovieFromCursor(Cursor c) {
        String cr = getStringValue("creators", c);

        List<KPPeople[]> p = (new Gson()).fromJson(cr, MOVIE_TYPE);

        return new KPMovie(
                getStringValue("filmID", c),
                getStringValue("nameRU", c),
                getStringValue("nameEN", c),
                getStringValue("year", c),
                getStringValue("filmLength", c),
                getStringValue("country", c),
                getStringValue("genre", c),
                getStringValue("description", c),
                getStringValue("ratingMPAA", c),
                getStringValue("ratingAgeLimits", c),
                getStringValue("type", c),
                p,
                getStringValue("shortDescription", c),
                getStringValue("stars", c)
        );
    }

    public static MoviesRepository getInstance(Context context) {
        return new MoviesRepository(context);
    }
}
