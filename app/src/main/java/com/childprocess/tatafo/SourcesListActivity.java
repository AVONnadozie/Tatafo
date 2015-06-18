package com.childprocess.tatafo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Admin on 17/06/2015.
 */
public class SourcesListActivity extends Activity {
    private feedDBAdapter dbHelper;
    private SimpleCursorAdapter dataAdapter;
    private ArrayList<String> FeedNameList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Tatafo");
        setContentView(R.layout.activity_sources_list);
        ListView FeedList = (ListView) findViewById(R.id.list_feeds);
        dbHelper = new feedDBAdapter(this);
        dbHelper.open();

        //dbHelper.deleteFeedSources(); //For debugging purposes only
        dbHelper.createDefaultFeedSources();
        displayListView();

        //Delete option for feed source
        FeedList.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> FeedList, View view, int i, long l) {
                // Toast.makeText(getApplicationContext(),pos)
                return false;
            }
        });

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
        getMenuInflater().inflate(R.menu.activity_sources_list, menu);
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

        LayoutInflater factory = LayoutInflater.from(this);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setIcon(R.mipmap.ic_launcher).setTitle("Enter New Feed Data:").setView(layout).setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked OK so do some stuff */
                        if (!feedname.getText().toString().isEmpty()) {
                            try {
                                URL url = new URL(feedurl.getText().toString());
                                dbHelper.createFeedSource(feedname.getText().toString(), feedurl.getText().toString());
                                displayListView();

                            } catch (MalformedURLException e) {
                                Toast.makeText(getApplicationContext(),
                                        "Invalid URL, URLs should be in the format http://www.domain.com",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Source name is empty",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
     /*
     * User clicked cancel so do some stuff
     */
                    }
                });
        alert.show();
    }

    private void displayListView() {
        Cursor cursor = dbHelper.fetchAllfeeds();
        String[] columns = new String[]{
                feedDBAdapter.KEY_Url,
                feedDBAdapter.KEY_NAME


        };
        int[] to = new int[]{
                R.id.url,
                R.id.name,

        };
        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.add_item,
                cursor,
                columns,
                to,
                0);
        ListView listView = (ListView) findViewById(R.id.list_feeds);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                String Url = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Intent i = new Intent(SourcesListActivity.this, SplashActivity.class);
                i.putExtra("feed_url", Url);
                startActivity(i);
            }
        });

        EditText myFilter = (EditText) findViewById(R.id.filtertext);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                return dbHelper.fetchfeedByName(constraint.toString());
            }
        });

    }


}
