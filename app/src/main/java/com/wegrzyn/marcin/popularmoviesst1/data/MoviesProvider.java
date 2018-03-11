package com.wegrzyn.marcin.popularmoviesst1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Marcin Węgrzyn on 10.03.2018.
 * wireamg@gmail.com
 */

public class MoviesProvider extends ContentProvider {


    public static final int MOVIES = 100;
    public static final int ID_MOVIE = 101;

    public static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES, MOVIES);

        URI_MATCHER.addURI(MovieContract.CONTENT_AUTHORITY,
                MovieContract.PATH_MOVIES + "/#", ID_MOVIE);

    }

    private MoviesDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;


        int match = URI_MATCHER.match(uri);

        switch (match){
            case MOVIES:
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection
                        ,selectionArgs,null,null,sortOrder);
                break;
            case ID_MOVIE:
                selection = MovieContract.MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection
                        ,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = URI_MATCHER.match(uri);

        switch (match){
            case MOVIES:
                return insertMovie(uri, values);
            default:  throw new IllegalArgumentException("Not supported");
        }
    }

    private Uri insertMovie(Uri uri, ContentValues values){

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long id = database.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}