package com.yellowbambara.tatafo;

import android.database.AbstractCursor;

import com.yellowbambara.tatafo.parser.RSSFeed;
import com.yellowbambara.tatafo.parser.RSSItem;

/**
 * Created by Admin on 09/07/2015.
 */
public class FeedCursor extends AbstractCursor {
    private RSSFeed feed;
    public static final int _ID = 0;
    public static final int TITLE = 1;
    public static final int CONTENT = 2;
    public static final int DATE = 3;
    public static final int IMAGE = 4;
    public static final int TYPE = 5;

    public FeedCursor(RSSFeed feed) {
        this.feed = feed;
    }

    @Override
    public int getCount() {
        if (feed == null){
            return 0;
        }
        return feed.getItemCount();
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"_id", "title", "content", "date", "image", "type"};
    }

    @Override
    public String getString(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case _ID:
                    return String.valueOf(i);
                case TITLE:
                    return rssItem.getTitle();
                case CONTENT:
                    return rssItem.getContent();
                case DATE:
                    return rssItem.getDate();
                case IMAGE:
                    return rssItem.getImage();
                case TYPE:
                    return String.valueOf(rssItem.getType());
                default:
                    return "";
            }
        } else
            return "";
    }

    @Override
    public short getShort(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case _ID:
                    return (short) i;
                case TYPE:
                    return (short) rssItem.getType();
                case TITLE:
                case CONTENT:
                case DATE:
                case IMAGE:
                default:
                    return 0;
            }
        } else
            return 0;
    }

    @Override
    public int getInt(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case _ID:
                    return i;
                case TYPE:
                    return rssItem.getType();
                case TITLE:
                case CONTENT:
                case DATE:
                case IMAGE:
                default:
                    return 0;
            }
        } else
            return 0;
    }

    @Override
    public long getLong(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case _ID:
                    return i;
                case TYPE:
                    return rssItem.getType();
                case TITLE:
                case CONTENT:
                case DATE:
                case IMAGE:
                default:
                    return 0L;
            }
        } else
            return 0L;
    }

    @Override
    public float getFloat(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case _ID:
                    return i;
                case TYPE:
                    return rssItem.getType();
                case TITLE:
                case CONTENT:
                case DATE:
                case IMAGE:
                default:
                    return 0;
            }
        } else
            return 0;
    }

    @Override
    public double getDouble(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case _ID:
                    return i;
                case TYPE:
                    return rssItem.getType();
                case TITLE:
                case CONTENT:
                case DATE:
                case IMAGE:
                default:
                    return 0;
            }
        } else
            return 0;
    }

    @Override
    public boolean isNull(int i) {
        RSSItem rssItem = feed.getItem(getPosition());
        if (rssItem != null) {
            switch (i) {
                case TITLE:
                    return rssItem.getTitle() == null;
                case CONTENT:
                    return rssItem.getContent() == null;
                case DATE:
                    return rssItem.getDate() == null;
                case IMAGE:
                    return rssItem.getImage() == null;
                case TYPE:
                case _ID:
                    return false;
                default:
                    return true;
            }
        } else
            return true;
    }

}
