package com.yellowbambara.tatafo;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.yellowbambara.tatafo.R;
import com.yellowbambara.tatafo.parser.RSSFeed;

public class DetailActivity extends FragmentActivity {

    private RSSFeed feed;
    private int pos;
    private ContentAdapter adapter;
    private ViewPager pager;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setIcon(R.mipmap.ic_launcher);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        // Get the feed object and the position from the Intent
        feed = (RSSFeed) getIntent().getExtras().get("feed");
        pos = getIntent().getExtras().getInt("pos");

        // Initialize the views
        adapter = new ContentAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);

        // Set Adapter to pager:
        pager.setAdapter(adapter);
        pager.setCurrentItem(pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.share_option);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) shareItem
                .getActionProvider();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, feed.getItem(pos).getTitle());
        String shareBody =  feed.getItem(pos).getContent();
        String stripped = Utility.stripFeedTags(shareBody);
        shareIntent.putExtra(Intent.EXTRA_TEXT, stripped + "\n\nShared from " + R.string.app_name);

        // Set the share intent
        mShareActionProvider.setShareIntent(shareIntent);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; finish activity to go home
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ContentAdapter extends FragmentStatePagerAdapter {
        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return feed.getItemCount();
        }

        @Override
        public Fragment getItem(int position) {

            DetailFragment frag = new DetailFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("feed_item", feed.getItem(position));
            frag.setArguments(bundle);

            return frag;
        }

    }

}
