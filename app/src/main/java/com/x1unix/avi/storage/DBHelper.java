package com.x1unix.avi.storage;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "avi", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Init db
        createMovies(db);
        createFavorites(db);
        createViewed(db);
    }

    private void createMovies(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE movies (\n" +
                "    filmID          INT           PRIMARY KEY\n" +
                "                                  UNIQUE\n" +
                "                                  NOT NULL,\n" +
                "    nameRU          VARCHAR (128) DEFAULT \"\",\n" +
                "    nameEN          VARCHAR (128) DEFAULT \"\",\n" +
                "    year            VARCHAR (12)  DEFAULT \"\",\n" +
                "    filmLength      VARCHAR (10)  DEFAULT 0,\n" +
                "    county          VARCHAR (48)  DEFAULT \"\",\n" +
                "    genre           VARCHAR (64)  DEFAULT \"\",\n" +
                "    description     TEXT          DEFAULT \"\",\n" +
                "    ratingMPAA      VARCHAR (10)  DEFAULT \"\",\n" +
                "    ratingAgeLimits VARCHAR (5)   DEFAULT \"\",\n" +
                "    type            VARCHAR (32)  DEFAULT \"\",\n" +
                "    creators        BLOB          DEFAULT \"\"\n" +
                ");");
    }

    private void createFavorites(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE favorites (\n" +
                "    filmID INT PRIMARY KEY\n" +
                "             REFERENCES movies (filmID) \n" +
                "             NOT NULL\n" +
                "             UNIQUE\n" +
                ");\n");
    }

    private DBHelper createViewed(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE viewed (\n" +
                "    filmID INT PRIMARY KEY\n" +
                "             REFERENCES movies (filmID) \n" +
                "             NOT NULL\n" +
                "             UNIQUE\n" +
                ");\n");

        return this;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}