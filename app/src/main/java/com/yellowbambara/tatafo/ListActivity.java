package com.yellowbambara.tatafo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yellowbambara.tatafo.R;
import com.yellowbambara.tatafo.data.FeedContract;
import com.yellowbambara.tatafo.image.ImageLoader;
import com.yellowbambara.tatafo.parser.DOMParser;
import com.yellowbambara.tatafo.parser.RSSFeed;

import android.support.v7.widget.Toolbar;

import java.io.File;

public class ListActivity extends Activity {

    private RSSFeed feed;
    private ListView lv;
    private CustomListAdapter adapter;
    private String feedLink;
    private Toolbar toolbar;
    private ImageView iv;
    private TextView tvTitle;
    private TextView tvDate;
    private boolean isLarge;
    private String author;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_list);

        // set the feed link for refresh
        Bundle b = getIntent().getExtras();
        feedLink = b.getString("feed_url");
        feed = (RSSFeed) b.get("feed");

        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);


        Uri uri = FeedContract.buildUriForSourceUri(feedLink);
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(FeedContract.COLUMN_NAME);
            author = cursor.getString(index);
        } else {
            author = "";
        }


        if (author.isEmpty()) {
            actionBar.setTitle("Tatafo");
        } else {
            actionBar.setTitle(author);
        }

        // Initialize the variables:
        lv = (ListView) findViewById(R.id.listView);
        lv.setVerticalFadingEdgeEnabled(true);

        // Set an Adapter to the ListView
        adapter = new CustomListAdapter(this);
        if (adapter != null && !adapter.isEmpty()) {
            lv.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "News not currently available for this source", Toast.LENGTH_LONG).show();
        }

        // Set on item click listener to the ListView
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // actions to be performed when a list item clicked

                int pos = arg2;
                Bundle bundle = new Bundle();
                bundle.putSerializable("feed", feed);
                if (isLarge) {

                } else {
                    Intent intent = new Intent(ListActivity.this, DetailActivity.class);
                    intent.putExtras(bundle);
                    intent.putExtra("pos", pos);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_list, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_option:
                displaydiaog();
                return true;

            case R.id.refresh_option:
                refreshList(item);
                return (true);

            case R.id.about_option:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return (true);

        }
        return super.onOptionsItemSelected(item);
    }

    void displaydiaog() {
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
                                    Toast.makeText(getApplicationContext(), author + " could not be deleted", Toast.LENGTH_SHORT)
                                            .show();
                                }
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

    public void refreshList(final MenuItem item) {
        /* Attach a rotating ImageView to the refresh item as an ActionView */
        LayoutInflater inflater = (LayoutInflater) getApplication()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.action_refresh,
                null);

        Animation rotation = AnimationUtils.loadAnimation(getApplication(),
                R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        item.setActionView(iv);

        // trigger feed refresh:
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DOMParser tmpDOMParser = new DOMParser();
                feed = tmpDOMParser.parseXml(feedLink);

                ListActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (feed != null && feed.getItemCount() > 0) {
                            adapter.notifyDataSetChanged();
                            item.getActionView().clearAnimation();
                            item.setActionView(null);
                        }
                    }
                });
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.imageLoader.clearCache();
        adapter.notifyDataSetChanged();
    }

    // List adapter class
    class CustomListAdapter extends BaseAdapter {
        public static final int TYPE_HOME = 0;
        private LayoutInflater layoutInflater;
        public ImageLoader imageLoader;

        public CustomListAdapter(ListActivity activity) {
            layoutInflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader = new ImageLoader(activity.getApplicationContext());
        }

        @Override
        public int getCount() {
            // Set the total list item count
            return feed.getItemCount();
        }

        @Override
        public int getViewTypeCount() {
            return feed.getItemCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItem = convertView;

            // Initialize the views in the layout
            int pos = getItemViewType(position);
            if (listItem == null) {
                if (pos == 0) {
                    listItem = layoutInflater.inflate(R.layout.feed_row_highlighted, null);

                    iv = (ImageView) listItem.findViewById(R.id.thumb);

                    tvTitle = (TextView) listItem.findViewById(R.id.title);
                    tvDate = (TextView) listItem.findViewById(R.id.date);
                    imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv, false, getApplicationContext());
                    tvTitle.setText(feed.getItem(pos).getTitle());
                    tvDate.setText(feed.getItem(pos).getDate());
                } else {
                    listItem = layoutInflater.inflate(R.layout.feed_row, null);
                }
                iv = (ImageView) listItem.findViewById(R.id.thumb);
                tvTitle = (TextView) listItem.findViewById(R.id.title);
                tvDate = (TextView) listItem.findViewById(R.id.date);
                imageLoader.DisplayImage(feed.getItem(pos).getImage(), iv, false, getApplicationContext());

                tvTitle.setText(feed.getItem(pos).getTitle());
                tvDate.setText(feed.getItem(pos).getDate());
            }

            return listItem;
        }

    }

}
