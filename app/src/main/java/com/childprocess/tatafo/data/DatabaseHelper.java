package com.childprocess.tatafo.data;

/**
 * Created by Admin on 17/06/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_TABLE = "sources";
    public static final String COLUMN_ROW_ID = "_id";
    public static final String COLUMN_NAME = "name";//feed_name
    public static final String COLUMN_URL = "url"; //feed_url
    public static final String COLUMN_IS_DEFAULT = "is_default"; //feed_url

    private static final String DATABASE_NAME = "feeds.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE if not exists " + DB_TABLE + " (" +
                COLUMN_ROW_ID + " integer PRIMARY KEY autoincrement," +
                COLUMN_URL + "," +
                COLUMN_NAME + "," +
                COLUMN_IS_DEFAULT + " tinyint(1)," +
                " UNIQUE (" + COLUMN_URL + "));";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }
}
