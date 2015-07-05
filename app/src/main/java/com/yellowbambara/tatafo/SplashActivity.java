package com.yellowbambara.tatafo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import com.yellowbambara.tatafo.R;
import com.yellowbambara.tatafo.data.FeedContract;
import com.yellowbambara.tatafo.parser.DOMParser;
import com.yellowbambara.tatafo.parser.RSSFeed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SplashActivity extends Activity {

    private String RSS_FEED_URL;
    private RSSFeed feed;
    private String fileName;
    private AsyncLoadXMLFeed async;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setIcon(R.mipmap.ic_launcher);
        setContentView(R.layout.splash);
        if (savedInstanceState != null && savedInstanceState.containsKey("feed")) {
            loadFeed(savedInstanceState.getString("feed_url"));
        } else {
            loadFeed(getIntent().getStringExtra("feed_url"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feed_url", RSS_FEED_URL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(async != null){
            async.cancel(true);
        }
    }

    private void loadFeed(String feedUrl) {
        RSS_FEED_URL = feedUrl;

        ImageView iv = (ImageView) findViewById(R.id.imageView2);
        Animation rotation = AnimationUtils.loadAnimation(getApplication(),
                R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        Uri uri = FeedContract.buildUriForSourceUri(feedUrl);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        File feedFile = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(FeedContract.COLUMN_NAME);
            fileName = cursor.getString(index) + ".td";
            feedFile = getBaseContext().getFileStreamPath(fileName);
        } else {
            fileName = "";
        }

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() == null) {
            // No connectivity. Check if feed File for this feed exists
            if (fileName.isEmpty() || feedFile == null || !feedFile.exists()) {
                // No connectivity & Feed file doesn't exist: Show alert to exit
                // & check for connectivity
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(
                        "Unable to reach server, \nPlease check your connectivity.")
                        .setTitle("Tatafo")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // No connectivty and file exists: Read feed from the File
                Toast toast = Toast.makeText(this,
                        "No connectivity! Reading last update...",
                        Toast.LENGTH_LONG);
                toast.show();
                feed = ReadFeed(fileName);
                startListActivity(feed);
            }

        } else {
            // Connected - Start parsing
            async = new AsyncLoadXMLFeed();
            async.execute();
        }
    }

    private void startListActivity(RSSFeed feed) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", feed);
        bundle.putString("feed_url",RSS_FEED_URL);

        // launch List activity
        Intent intent = new Intent(SplashActivity.this, ListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

        // kill this activity
        finish();
    }

    // Method to write the feed to the File
    private void WriteFeed(RSSFeed data) {

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        try {
            fOut = openFileOutput(fileName, MODE_PRIVATE);
            osw = new ObjectOutputStream(fOut);
            osw.writeObject(data);
            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to read the feed from the File
    private RSSFeed ReadFeed(String fName) {

        FileInputStream fIn = null;
        ObjectInputStream isr = null;

        RSSFeed feed = null;
        File feedFile = getBaseContext().getFileStreamPath(fileName);
        if (!feedFile.exists())
            return null;

        try {
            fIn = openFileInput(fName);
            isr = new ObjectInputStream(fIn);

            feed = (RSSFeed) isr.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return feed;

    }

    private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Obtain feed
            DOMParser myParser = new DOMParser();
            feed = myParser.parseXml(RSS_FEED_URL);
            if (feed != null && feed.getItemCount() > 0)
                WriteFeed(feed);
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startListActivity(feed);
        }

    }

}
