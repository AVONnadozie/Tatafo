package com.yellowbambara.tatafo.parser;

import android.database.AbstractCursor;
import android.database.Cursor;

import com.yellowbambara.tatafo.FeedCursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RSSFeed implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<RSSItem> itemList;
    private String author = "";

    public RSSFeed() {
        itemList = new ArrayList<>();
    }

    public void addItem(RSSItem item) {
        if (itemList != null) {
            itemList.add(item);
        }
    }

    public RSSItem getItem(int location) {
        if (itemList == null || location < 0 || location >= itemList.size())
            return null;

        return itemList.get(location);
    }

    public int getItemCount() {
        if (itemList == null) {
            return 0;
        }
        return itemList.size();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Cursor getCursor() {
        if (itemList == null) {
            return null;
        } else {
            return new FeedCursor(this);
        }
    }
}
