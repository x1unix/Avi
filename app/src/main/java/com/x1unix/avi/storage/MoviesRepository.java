package com.x1unix.avi.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.appcompat.BuildConfig;
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
    private static final String LOG_TAG = "MoviesRepository";
    public static final Type MOVIE_TYPE = new TypeToken<ArrayList<List<KPPeople[]>>>() {
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
        cv.put("county", movie.getCountry());
        cv.put("genre", movie.getGenre());
        cv.put("description", movie.getDescription());
        cv.put("ratingMPAA", movie.getRatingMPAA());
        cv.put("ratingAgeLimits", movie.getRatingAgeLimits());
        cv.put("type", movie.getType());

        // Serialize data to JSON and save as BLOB
        cv.put("creators", (new Gson()).toJson(movie.getCreators()));

        // Write movie to db
        long rowID = db.insert(MoviesRepository.TABLE_MOVIES, null, cv);
        Log.d(MoviesRepository.LOG_TAG, "Movie row inserted, ID: " + rowID);

        return this;
    }

    public boolean movieExists(String kpId) {
        String query = "SELECT * FROM " + MoviesRepository.TABLE_MOVIES + " where filmID = " + kpId;
        Cursor cursor = db.rawQuery(query, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();

        return exists;
    }

    private String getSelect() {
        return "SELECT * FROM " + MoviesRepository.TABLE_MOVIES;
    }

    public KPMovie getMovieById(String filmId) {


        KPMovie movie = null;
        Cursor c = db.rawQuery(getSelect() + " where filmID = " + filmId, null);

        try {
            if ((c != null) && c.moveToFirst()) {
                while (!c.isAfterLast()) {
                    movie = MoviesRepository.getMovieFromCursor(c);
                }
            }
        } catch (Exception e) {
            movie = null;
        }

        if (c != null) c.close();

        return movie;
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
        List<KPPeople[]> p = (new Gson()).fromJson(getStringValue("description", c), MoviesRepository.MOVIE_TYPE);

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
                p
        );
    }

    public static MoviesRepository getInstance(Context context) {
        return new MoviesRepository(context);
    }
}
