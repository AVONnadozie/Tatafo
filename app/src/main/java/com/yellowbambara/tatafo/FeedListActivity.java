package com.yellowbambara.tatafo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowbambara.tatafo.data.FeedContract;
import com.yellowbambara.tatafo.parser.DOMParser;
import com.yellowbambara.tatafo.parser.RSSFeed;
import com.yellowbambara.tatafo.parser.RSSItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FeedListActivity extends FragmentActivity {

    private RSSFeed feed;
    private String feed_url;
    private boolean isTwoPane;
    private String author;
    private AsyncLoadXMLFeed async;
    private String fileName;
    private MenuItem menuItem;
    private int currentListSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.feed_list);

        if (savedInstanceState != null) {
            feed_url = savedInstanceState.getString("feed_url");
            author = savedInstanceState.getString("author");
            currentListSelection = savedInstanceState.getInt("currentListSelection");
        } else {
            feed_url = getIntent().getStringExtra("feed_url");
            author = getIntent().getStringExtra("author");
            currentListSelection = 0;
        }

        fileName = author + ".td";
        actionBar.setTitle(author);
        isTwoPane = findViewById(R.id.detailFragment) != null;

        loadFeed();

        if (savedInstanceState == null && feed != null) {
            if (isTwoPane) {
                DetailFragment f = new DetailFragment();
                if (feed != null && feed.getItemCount() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("feed_item", feed.getItem(currentListSelection));
                    f.setArguments(bundle);
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detailFragment, f)
                        .commit();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        int orientation;
//        switch (getRequestedOrientation()) {
//            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
//            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
//            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
//            case ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE:
//                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                break;
//
//            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
//            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
//            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
//            case ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT:
//                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                break;
//            default:
//                orientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
//        }
//        setRequestedOrientation(orientation);
    }

    public void setCurrentListSelection(int currentListSelection) {
        this.currentListSelection = currentListSelection;
    }

    private void replaceFeedListFragment() {
        Bundle bundle = new Bundle();
        FeedListFragment feedListFragment = new FeedListFragment();
        bundle.putBoolean("twoPane", isTwoPane);
        bundle.putSerializable("feed", feed);
        bundle.putString("feed_url", feed_url);
        bundle.putInt("currentListSelection", currentListSelection);
        feedListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.list_fragment, feedListFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.activity_list, menu);

        menuItem = menu.findItem(R.id.refresh_option);
        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.share_option);
        if (feed != null && shareItem != null) {
            // Fetch and store ShareActionProvider
            ShareActionProvider mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            RSSItem feedItem = feed.getItem(currentListSelection);
            if (feedItem != null) {
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, feedItem.getTitle());
                String shareBody = feedItem.getContent();
                String stripped = Utility.stripXMLTags(shareBody);
                shareIntent.putExtra(Intent.EXTRA_TEXT, stripped + "\n\nShared from " + getString(R.string.app_name));

                // Set the share intent
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_option:
                displaydiaog();
                return true;

            case R.id.refresh_option:
                readFeedOnline(false);
                return (true);

            case R.id.about_option:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return (true);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feed_url", feed_url);
        outState.putString("author", author);
        outState.putInt("currentListSelection", currentListSelection);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (async != null) {
            async.cancel(true);
        }
    }

    private void loadFeed() {

        File feedFile = getBaseContext().getFileStreamPath(fileName);
        if (fileName.isEmpty() || feedFile == null || !feedFile.exists()) {
            readFeedOnline(true);
        } else {
            feed = readFeedFromFile(fileName);
            readFeedOnline(false);
            replaceFeedListFragment();
        }

    }

    private void readFeedOnline(boolean showErrorDialog) {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() == null) {
            if (showErrorDialog) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Tatafo")
                        .setMessage("Internet has left\nPlease check your connectivity.")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        } else {
            // Connected - Start parsing
            async = new AsyncLoadXMLFeed();
            async.execute();
        }
    }

    // Method to write the feed to the File
    private void WriteFeed(RSSFeed data) {

        FileOutputStream fOut = null;
        ObjectOutputStream osw;

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
    private RSSFeed readFeedFromFile(String fName) {

        FileInputStream fIn = null;
        ObjectInputStream isr;

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
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "Reading top stories", Toast.LENGTH_LONG).show();
            if (menuItem != null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView) inflater.inflate(R.layout.action_refresh, null);

                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.refresh_rotate);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);

                menuItem.setActionView(iv);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Obtain feed
            DOMParser myParser = new DOMParser();
            feed = myParser.parseXml(feed_url);
            if (feed != null && feed.getItemCount() > 0)
                WriteFeed(feed);
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (feed != null && feed.getItemCount() > 0) {
                replaceFeedListFragment();
                if (menuItem != null) {
                    View actionView = menuItem.getActionView();
                    if (actionView != null) {
                        actionView.clearAnimation();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "We were currently unable to fetch stories from this source, we will try again soon",
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    private void displaydiaog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView view = new TextView(getApplicationContext());
        view.setTextColor(Color.BLACK);
        view.setText("Do you want to delete " + author + "?");
        layout.addView(view);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setIcon(R.mipmap.ic_launcher)
                .setTitle("Confirm")
                .setView(layout)
                .setPositiveButton("Yes, I am Sure",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteFeedSource();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Do nothing
                            }
                        });
        alert.show();
    }

    private void deleteFeedSource() {
        //Delete feed file
        String fileName = author + ".td";
        File feedFile = getBaseContext().getFileStreamPath(fileName);
        feedFile.delete();
        //Delete from database
        Uri uri = FeedContract.buildUriForSourceName(author);
        int row = getContentResolver().delete(uri, null, null);
        if (row > 0) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Oops! " + author + " could not be deleted", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
