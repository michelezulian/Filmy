package com.michelezulian.example.filmy_projectwork.DBinternal;

import android.provider.BaseColumns;

public class MovieTableHelper implements BaseColumns {
    public static final String TABLE_NAME = "movie";
    public static final String TITLE = "title";
    public static final String MOVIE_ID = "movie_id";
    public static final String OVERVIEW = "overview";
    public static final String POSTER_PATH = "poster_path";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String SEEN = "seen";
    public static final String WISHLIST = "wishlist";


    public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " ( " +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " +
            MOVIE_ID + " TEXT, " +
            OVERVIEW + " INTEGER, " +
            SEEN + " INTEGER, " +
            WISHLIST + " INTEGER, " +
            POSTER_PATH + " INTEGER, " +
            BACKDROP_PATH + " INTEGER) ; ";
}