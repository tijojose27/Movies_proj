package com.example.tijo.movies_proj.Database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by tijoj on 2/8/2018.
 */

public class MoviesContract {

    public static final String AUTHORITY = "com.example.tijo.movies_proj";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_IMAGE_URL = "img_url";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE ="releaseDate";
        public static final String COLUMN_IMAGE_URL_ORIGINAL = "img_url_original";



    }

}
