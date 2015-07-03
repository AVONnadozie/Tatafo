package com.childprocess.tatafo.data;

import android.content.ContentUris;
import android.net.Uri;

/**
 * Created by Admin on 23/06/2015.
 */
public class FeedContract {

    public static final String CONTENT_AUTHORITY = "com.childprocess.tatafo";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String TABLE_NAME = DatabaseHelper.DB_TABLE;
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build() ;
    public static final String CONTENT_TYPE = "vnd.andriod.cursor.dir/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.andriod.cursor.item/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
    public static final String COLUMN_URI = DatabaseHelper.COLUMN_URL;
    public static final String COLUMN_NAME = DatabaseHelper.COLUMN_NAME;
    public static final String COLUMN_DEFAULT = DatabaseHelper.COLUMN_IS_DEFAULT;

    public static Uri buildFeedUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI,id);
    }

    public static Uri buildUriForSourceUri(String sUri){
        return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_URI,sUri).build();
    }

    public static Uri buildUriForSourceName(String sUri){
        return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_NAME, sUri).build();
    }

    public static Uri buildUriForIsDefault(boolean isDefault){
        return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_DEFAULT, isDefault ? "1" : "0").build();
    }

    public static String getSourceUriFromUri(Uri uri){
        return uri.getQueryParameter(COLUMN_URI);
    }

    public static String getSourceNameFromUri(Uri uri){
        return uri.getQueryParameter(COLUMN_NAME);
    }

    public static boolean getIsDefaultFromUri(Uri uri){
        return uri.getQueryParameter(COLUMN_DEFAULT).equals("1");
    }
}
