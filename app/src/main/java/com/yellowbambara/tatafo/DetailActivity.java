package com.yellowbambara.tatafo;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.yellowbambara.tatafo.parser.RSSItem;

public class DetailActivity extends FragmentActivity {

    private RSSItem item;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setIcon(R.mipmap.ic_launcher);

        // Get the item object and the position from the Intent
        item = (RSSItem) getIntent().getExtras().get("feed_item");
        if(savedInstanceState == null) {
            DetailFragment frag = new DetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("feed_item", item);
            frag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailFragment, frag)
                    .commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(item != null) {
            new MenuInflater(this).inflate(R.menu.activity_detail, menu);
            // Locate MenuItem with ShareActionProvider
            MenuItem shareItem = menu.findItem(R.id.share_option);
            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) shareItem
                    .getActionProvider();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
            String shareBody = item.getContent();
            String stripped = Utility.stripXMLTags(shareBody);
            shareIntent.putExtra(Intent.EXTRA_TEXT, stripped + "\n\nShared from " + R.string.app_name);

            // Set the share intent
            mShareActionProvider.setShareIntent(shareIntent);
            return true;
        }else{
            return false;
        }
    }

}
