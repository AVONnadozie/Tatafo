package com.childprocess.tatafo.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RSSFeed implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<RSSItem> _itemlist;

    RSSFeed() {
        _itemlist = new ArrayList<>();
    }

    void addItem(RSSItem item) {
        _itemlist.add(item);
    }

    public RSSItem getItem(int location) {
        return _itemlist.get(location);
    }

    public int getItemCount() {
        return _itemlist.size();
    }

}
