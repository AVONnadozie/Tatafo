package com.yellowbambara.tatafo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Admin on 26/06/2015.
 */
public class DatabaseAdapter {

    private SQLiteDatabase db;
    private DatabaseHelper helper;

    public DatabaseAdapter(Context context){
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
        createDefaultFeedSources();
    }

    public void close(){
        if (helper != null){
            helper.close();
        }
    }

    public SQLiteDatabase getDb(){
        return db;
    }

    public long createFeedSource(ContentValues initialValues) {
        initialValues.put(DatabaseHelper.COLUMN_IS_DEFAULT, 0);
        //Do not make changes if feed_row_default already exists
        return db.insertWithOnConflict(DatabaseHelper.DB_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public int delete(String url, String name) {
        //Modified where clause to make sure a default clause is not deleted
        if (url != null && !url.isEmpty()) {
            return db.delete(DatabaseHelper.DB_TABLE,
                    DatabaseHelper.COLUMN_URL + " = '" + url + "'",
                    null);
        } else {
            return db.delete(DatabaseHelper.DB_TABLE,
                    DatabaseHelper.COLUMN_NAME + " = '" + name + "'",
                    null);
        }
    }

    public int update(String url, String name, ContentValues contentValues) {
        //Modified where clause to make sure a default clause is not updated
        if (url != null && !url.isEmpty()) {
            return db.update(DatabaseHelper.DB_TABLE,
                    contentValues,
                    DatabaseHelper.COLUMN_URL + " = '" + url + "' and " + DatabaseHelper.COLUMN_IS_DEFAULT + " = 0",
                    null);
        } else {
            return db.update(DatabaseHelper.DB_TABLE,
                    contentValues,
                    DatabaseHelper.COLUMN_NAME + " = '" + name + "' and " + DatabaseHelper.COLUMN_IS_DEFAULT + " = 0",
                    null);
        }

    }

    private long createFeedSource(String name, String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(DatabaseHelper.COLUMN_URL, url);
        initialValues.put(DatabaseHelper.COLUMN_NAME, name);
        initialValues.put(DatabaseHelper.COLUMN_IS_DEFAULT, 1);

        //Do not make changes if feed_row_default already exists
        return db.insertWithOnConflict(DatabaseHelper.DB_TABLE, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void createDefaultFeedSources() {
        createFeedSource("Linda Ikeji", "http://feeds.feedburner.com/blogspot/OqshX");
        createFeedSource("Naija Loaded", "http://www.naijaloaded.com.ng/feed/");
//        createFeedSource("Bella Naija", "http://www.bellanaija.com/feed/");
        createFeedSource("Too Xclusive", "http://www.tooxclusive.com/feed/");
        createFeedSource("Information NG", "http://www.informationng.com/feed");
        createFeedSource("Sleek Naija", "http://www.sleeknaija.com/feed/");
//        createFeedSource("YNaija", "http://ynaija.com/feed/");
        createFeedSource("360nobs", "http://www.360nobs.com/feed/");
        createFeedSource("Pulse.ng", "http://www.pulse.ng/rss/");
        createFeedSource("Naij", "http://www.naij.com/feed/");
    }
}
