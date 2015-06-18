package com.childprocess.tatafo;

/**
 * Created by Admin on 17/06/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class feedDBAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "code";//feed_name
    public static final String KEY_Url = "name"; //feed_url


    private static final String TAG = "feedDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "Feeds";
    private static final String SQLITE_TABLE = "all_feeds";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_Url + "," +
                    KEY_NAME + "," +

                    " UNIQUE (" + KEY_Url + "));";


    private boolean deleteSingle(String key) {
        //  String query = "DELETE FROM " + SQLITE_TABLE + " WHERE " + KEY_Url + " = '" + key + "'";
        return mDb.delete(SQLITE_TABLE, KEY_Url + " = '" + key + "'", null) > 0;

    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
            onCreate(db);
        }
    }

    public feedDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public feedDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public long createFeed(String name,
                           String url) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Url, url);
        initialValues.put(KEY_NAME, name);


        return mDb.insert(SQLITE_TABLE, null, initialValues);
    }


    public Cursor fetchfeedByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null || inputText.length() == 0) {
            mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                            KEY_Url, KEY_NAME},
                    null, null, null, null, null);

        } else {
            mCursor = mDb.query(true, SQLITE_TABLE, new String[]{KEY_ROWID,
                            KEY_Url, KEY_NAME},
                    KEY_NAME + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetchAllfeeds() {

        Cursor mCursor = mDb.query(SQLITE_TABLE, new String[]{KEY_ROWID,
                        KEY_Url, KEY_NAME},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean deleteFeedSources() {
        int doneDelete = 0;
        doneDelete = mDb.delete(SQLITE_TABLE, null, null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;
    }

    public void createDefaultFeedSources() {
        createFeed("Linda Ikeji", "http://feeds.feedburner.com/blogspot/OqshX");
        createFeed("Naija Loaded", "http://www.naijaloaded.com.ng/feed/");
        createFeed("Bella Naija", "http://www.bellanaija.com/feed/");
        createFeed("Too Xclusive", "http://www.tooxclusive.com/feed/");
        createFeed("Information NG", "http://www.informationng.com/feed");
        createFeed("Ladun Liadi", "https://www.blogger.com/feeds/5820553792761826908/posts/default");
        createFeed("Sleek Naija", "http://www.sleeknaija.com/feed/");
        createFeed("YNaija", "http://ynaija.com/feed/");
        createFeed("360nobs", "http://www.360nobs.com/feed/");
        createFeed("Pulse.ng", "http://www.pulse.ng/rss/");
        createFeed("Naij", "http://www.naij.com/feed/");
    }

}
