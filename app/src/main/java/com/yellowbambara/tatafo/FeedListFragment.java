package com.yellowbambara.tatafo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.yellowbambara.tatafo.image.ImageLoader;
import com.yellowbambara.tatafo.parser.RSSFeed;
import com.yellowbambara.tatafo.parser.RSSItem;

/**
 * Created by Admin on 08/07/2015.
 */
public class FeedListFragment extends Fragment {
    private ListView lv;
    private CustomAdapter adapter;
    private int currentSelection;
    private boolean isTwoPane;
    private String feed_url;
    private RSSFeed feed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            isTwoPane = arguments.getBoolean("twoPane");
            feed = (RSSFeed) arguments.getSerializable("feed");
            feed_url = arguments.getString("feed_url");
            currentSelection = arguments.getInt("currentListSelection");
        } else if (savedInstanceState != null) {
            currentSelection = savedInstanceState.getInt("currentSelection");
            isTwoPane = savedInstanceState.getBoolean("twoPane");
            feed_url = savedInstanceState.getString("feed_url");
            feed = (RSSFeed) savedInstanceState.getSerializable("feed");
        }

        View view = inflater.inflate(R.layout.feedlist_fragment, container, false);
        // Initialize the variables:
        lv = (ListView) view.findViewById(R.id.listView);
        lv.setVerticalFadingEdgeEnabled(true);

        // Set custom Adapter
        adapter = new CustomAdapter(getActivity(), new FeedCursor(feed), 0);
        if (adapter != null && !adapter.isEmpty()) {
            adapter.setCanUseHighlightedRow(!isTwoPane);
            lv.setAdapter(adapter);
            if (currentSelection != ListView.INVALID_POSITION) { //Note to me: Move to onLoadFinish
                lv.smoothScrollToPosition(currentSelection);
            }
        }

        // Set on item click listener to the ListView
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                currentSelection = position;
                view.setSelected(true);
                ((FeedListActivity) getActivity()).setCurrentListSelection(currentSelection);
                Bundle bundle = new Bundle();
                if (isTwoPane) {
                    DetailFragment f = new DetailFragment();
                    bundle.putSerializable("feed_item", feed.getItem(currentSelection));
                    f.setArguments(bundle);
                    //Display on fragment
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.detailFragment, f)
                            .commit();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    bundle.putSerializable("feed_item", feed.getItem(currentSelection));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentSelection != ListView.INVALID_POSITION) {
            outState.putInt("currentSelection", currentSelection);
        } else {
            outState.putInt("currentSelection", 0);
        }
        outState.putBoolean("twoPane", isTwoPane);
        outState.putString("feed_url", feed_url);
        outState.putSerializable("feed", feed);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.imageLoader.clearCache();
        adapter.notifyDataSetChanged();

    }

    //Custom CursorAdapter
    class CustomAdapter extends CursorAdapter {

        public final ImageLoader imageLoader;
        private final int TYPE_HIGHLIGHTED_ROW = 0;
        private final int TYPE_DEFAULT_ROW = 1;
        private boolean canUseHighlightedRow;

        public CustomAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            imageLoader = new ImageLoader(context);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public void setCanUseHighlightedRow(boolean b) {
            canUseHighlightedRow = b;
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0 && canUseHighlightedRow) ? TYPE_HIGHLIGHTED_ROW : TYPE_DEFAULT_ROW;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view;
            int position = cursor.getPosition();
            switch (getItemViewType(position)) {
                case TYPE_HIGHLIGHTED_ROW:
                    view = LayoutInflater.from(context).inflate(R.layout.feed_row_highlighted, null);
                    break;
                case TYPE_DEFAULT_ROW:
                    view = LayoutInflater.from(context).inflate(R.layout.feed_row_default, null);
                    break;
                default:
                    view = LayoutInflater.from(context).inflate(-1, parent);
            }

            ViewHolder holder = new ViewHolder(view);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            imageLoader.DisplayImage(cursor.getString(FeedCursor.IMAGE), holder.getImageView(), false, context);
            holder.getTextViewTitle().setText(cursor.getString(FeedCursor.TITLE));
            holder.getTextViewDate().setText(cursor.getString(FeedCursor.DATE));
        }
    }
}
