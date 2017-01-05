package com.x1unix.avi.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.x1unix.avi.model.KPMovie;

public class MoviesRepository {

    private Context context;
    private DBHelper dbHelper;

    public static MoviesRepository getInstance(Context context) {
        return new MoviesRepository(context);
    }

    public MoviesRepository(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);

    }

    public void addMovie(KPMovie movie) {

    }
}
