package com.yellowbambara.tatafo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.yellowbambara.tatafo.data.FeedContract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Admin on 17/06/2015.
 */
public class MainActivity extends Activity {
    private SimpleCursorAdapter dataAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(getString(R.string.app_name));
        setContentView(R.layout.activity_main);
        loadListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListView();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                displaydiaog();
                break;
            case R.id.about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds feeds to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    void displaydiaog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText feedname = new EditText(this);
        feedname.setHint("Source Name");
        layout.addView(feedname);

        final EditText feedurl = new EditText(this);
        feedurl.setHint("http://");
        layout.addView(feedurl);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setIcon(R.mipmap.ic_launcher)
                .setTitle("Enter New Feed Data:")
                .setView(layout)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String link = feedurl.getText().toString();
                                if (!link.isEmpty()) {
                                    try {
                                        addFeedUrl(link, feedname.getText().toString());
                                        loadListView();
                                    } catch (MalformedURLException e) {
                                        if (link.matches("[Hh][Tt]{2}[Pp][Ss]?://[\\S\\s]*")) { //if link has a http or https protocol
                                            Toast.makeText(getApplicationContext(),
                                                    "Invalid URL, URLs should be in the format http://www.domain.com",
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            //Attempt to correct URL by appending http suffix
                                            link = "http://" + link;
                                            try {
                                                addFeedUrl(link, feedname.getText().toString());
                                                loadListView();
                                            } catch (MalformedURLException e1) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Invalid URL, URLs should be in the format http://www.domain.com",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Source name is empty",
                                            Toast.LENGTH_LONG).show();

                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do nothing
                            }
                        });
        alert.show();
    }

    private void addFeedUrl(String link, String feedname) throws MalformedURLException {
        URL url = new URL(link);
        ContentValues v = new ContentValues(2);
        v.put(FeedContract.COLUMN_NAME, feedname);
        v.put(FeedContract.COLUMN_URI, url.toString());
        getContentResolver().insert(FeedContract.CONTENT_URI, v);
    }


    public void loadListView() {
        Cursor cursor = getContentResolver().query(FeedContract.CONTENT_URI,
                null,
                null,
                null,
                null);

        String[] columns = new String[]{
                FeedContract.COLUMN_URI,
                FeedContract.COLUMN_NAME
        };

        int[] to = new int[]{
                R.id.url,
                R.id.name,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.source_row,
                cursor,
                columns,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.list_feeds);
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding feed_row_default in the result set
                view.setSelected(true);
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String Url = cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.COLUMN_URI));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(FeedContract.COLUMN_NAME));
                Intent i = new Intent(MainActivity.this, FeedListActivity.class);
                i.putExtra("feed_url", Url);
                i.putExtra("author", name);
                startActivity(i);
            }
        });
    }

    private class LoaderLoadXMLFeed implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getApplicationContext(),
                    FeedContract.CONTENT_URI,
                    new String[]{
                            FeedContract.COLUMN_URI,
                            FeedContract.COLUMN_NAME
                    },
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            dataAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            dataAdapter.swapCursor(null);
        }
    }

}
