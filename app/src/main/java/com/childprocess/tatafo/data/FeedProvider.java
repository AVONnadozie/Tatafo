package com.childprocess.tatafo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;


/**
 * Created by Admin on 24/06/2015.
 */
public class FeedProvider extends ContentProvider {

    public static final int SOURCES = 100;
    public static final int SOURCES_WITH_URI = 101;
    public static final int SOURCES_WITH_NAME = 102;
    public static final int SOURCES_WITH_DEFAULT = 103;

    public static final UriMatcher uriMatcher = buildUriMatcher();
    DatabaseAdapter dbAdapter;

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = FeedContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FeedContract.TABLE_NAME, SOURCES);
        matcher.addURI(authority, FeedContract.TABLE_NAME + "/" + FeedContract.COLUMN_URI + "/*", SOURCES_WITH_URI);
        matcher.addURI(authority, FeedContract.TABLE_NAME + "/" + FeedContract.COLUMN_NAME + "/*", SOURCES_WITH_NAME);
        matcher.addURI(authority, FeedContract.TABLE_NAME + "/" + FeedContract.COLUMN_DEFAULT+ "/#", SOURCES_WITH_DEFAULT);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbAdapter = new DatabaseAdapter(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case SOURCES:
                cursor = dbAdapter.getDb()
                        .query(FeedContract.TABLE_NAME,
                                projection,
                                null,
                                null,
                                null,
                                null,
                                sortOrder);
                break;
            case SOURCES_WITH_URI:
                String sourceUri = FeedContract.getSourceUriFromUri(uri);
                cursor = dbAdapter.getDb()
                        .query(FeedContract.TABLE_NAME,
                                projection,
                                FeedContract.COLUMN_URI + "='" + sourceUri + "'",
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case SOURCES_WITH_NAME:
                String sourceName = FeedContract.getSourceNameFromUri(uri);
                cursor = dbAdapter.getDb()
                        .query(FeedContract.TABLE_NAME,
                                projection,
                                FeedContract.COLUMN_NAME + "='" + sourceName + "'",
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case SOURCES_WITH_DEFAULT:
                boolean isDefault = FeedContract.getIsDefaultFromUri(uri);
                cursor = dbAdapter.getDb()
                        .query(FeedContract.TABLE_NAME,
                                projection,
                                FeedContract.COLUMN_DEFAULT + "=" + (isDefault ? 1 : 0),
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SOURCES:
                return FeedContract.CONTENT_TYPE;
            case SOURCES_WITH_URI:
                return FeedContract.CONTENT_ITEM_TYPE;
            case SOURCES_WITH_NAME:
                return FeedContract.CONTENT_ITEM_TYPE;
            case SOURCES_WITH_DEFAULT:
                return FeedContract.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        switch (uriMatcher.match(uri)) {
            case SOURCES:
                long row = dbAdapter.createFeedSource(contentValues);
                if (row > 0) {
                    getContext().getContentResolver().notifyChange(uri,null);
                    return FeedContract.buildFeedUri(row);
                } else {
                    throw new SQLException("Failed to insert a row");
                }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case SOURCES_WITH_URI:
                String sourceUri = FeedContract.getSourceUriFromUri(uri);
                return dbAdapter.delete(sourceUri,"");
            case SOURCES_WITH_NAME:
                String sourceName = FeedContract.getSourceNameFromUri(uri);
                return dbAdapter.delete("",sourceName);
            case SOURCES_WITH_DEFAULT:
            case SOURCES:
                throw new UnsupportedOperationException("Unsupported Operation: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case SOURCES_WITH_URI:
                String sourceUri = FeedContract.getSourceUriFromUri(uri);
                return dbAdapter.update(sourceUri, "", contentValues);
            case SOURCES_WITH_NAME:
                String sourceName = FeedContract.getSourceNameFromUri(uri);
                return dbAdapter.update("", sourceName, contentValues);
            case SOURCES_WITH_DEFAULT:
            case SOURCES:
                throw new UnsupportedOperationException("Unsupported Operation: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }
}
