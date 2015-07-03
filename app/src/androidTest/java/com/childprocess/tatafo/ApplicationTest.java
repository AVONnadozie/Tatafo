package com.childprocess.tatafo;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.test.ApplicationTestCase;

import com.childprocess.tatafo.data.DatabaseAdapter;
import com.childprocess.tatafo.data.DatabaseHelper;
import com.childprocess.tatafo.data.FeedContract;

import org.apache.http.protocol.UriPatternMatcher;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testContract(){
        String uri = "Linda";
        Uri builtUri = FeedContract.buildUriForSourceName(uri);
        String sourceUriFromUri = FeedContract.getSourceNameFromUri(builtUri);
        assertEquals(uri,sourceUriFromUri);
    }

    public void testDBHelper(){
        DatabaseAdapter helper = new DatabaseAdapter(getContext());
        String url = "http://google.com";
        String name = "google";
        ContentValues values = new ContentValues(2);
        values.put(FeedContract.COLUMN_NAME,name);
        values.put(FeedContract.COLUMN_URI, url);

        long rows = helper.createFeedSource(values);
        assertEquals(true,rows > 0);

        values.remove(FeedContract.COLUMN_URI);
        values.put(FeedContract.COLUMN_URI, "http://www.google.com");
        int rows2 = helper.update(url, "", values);
        assertEquals(true, rows2 > 0);

        int rows3 = helper.delete("", name);
        assertEquals(true, rows3 > 0);
    }

    public void testProvider(){
        ContentResolver contentResolver = getContext().getContentResolver();
        String url = "http://google.com";
        String name = "google";
        ContentValues values = new ContentValues(2);
        values.put(FeedContract.COLUMN_NAME,name);
        values.put(FeedContract.COLUMN_URI, url);

        Uri insertUri = contentResolver.insert(FeedContract.CONTENT_URI, values);
        Uri expected = FeedContract.buildFeedUri(1);
        assertEquals(expected, insertUri);

        values.remove(FeedContract.COLUMN_URI);
        values.put(FeedContract.COLUMN_URI, "http://www.google.com");
        Uri uri = FeedContract.buildUriForSourceUri(url);
        int rows2 = contentResolver.update(uri,values,null,null);
        assertEquals(true, rows2 > 0);

        Uri uri2 = FeedContract.buildUriForSourceName(name);
        int rows3 = contentResolver.delete(uri2,null,null);
        assertEquals(true, rows3 > 0);
    }
}