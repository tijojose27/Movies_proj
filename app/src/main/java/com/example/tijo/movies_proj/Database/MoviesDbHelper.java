package com.example.tijo.movies_proj.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tijoj on 2/8/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";

    private static final int VERSION = 1;


    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE "+ MoviesContract.MoviesEntry.TABLE_NAME+ " ( "+
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY, "+
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID +" INTEGER NOT NULL, "+
                MoviesContract.MoviesEntry.COLUMN_TITLE+" TEXT, "+
                MoviesContract.MoviesEntry.COLUMN_SYNOPSIS+" TEXT, "+
                MoviesContract.MoviesEntry.COLUMN_IMAGE_URL+" TEXT, "+
                MoviesContract.MoviesEntry.COLUMN_RATING+" TINYINT, "+
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE+" TEXT, "+
                MoviesContract.MoviesEntry.COLUMN_IMAGE_URL_ORIGINAL+" TEXT);";

        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldDB, int newDB) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}