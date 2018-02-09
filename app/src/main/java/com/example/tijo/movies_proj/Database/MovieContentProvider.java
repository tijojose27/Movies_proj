package com.example.tijo.movies_proj.Database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by tijoj on 2/8/2018.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private MoviesDbHelper moviesDbHelper;

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES+"#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        moviesDbHelper = new MoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match){
            case MOVIES:
                retCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : "+uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES:
                long id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);

                if(id>0){
                    returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                }else {
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                }
                break;
        default:
            throw new UnsupportedOperationException("Uknown uri "+uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch (match) {
            case MOVIES_WITH_ID:

                String id = uri.getPathSegments().get(1);

                moviesDeleted = db.delete(MoviesContract.MoviesEntry.TABLE_NAME, "_id=?", new String[]{id});

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if(moviesDeleted!=0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;

    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Not update implementation needed");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not needed implemenation");
    }


}
