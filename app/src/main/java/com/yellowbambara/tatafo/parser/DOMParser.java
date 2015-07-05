package com.yellowbambara.tatafo.parser;

import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndContent;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;

import java.io.IOException;
import java.util.List;

public class DOMParser {

    private RSSFeed _feed = new RSSFeed();

    public RSSFeed parseXml(String xmlUrl) {

        try {
            SyndFeed f = RssAtomFeedRetriever.getMostRecentNews(xmlUrl);
            List<SyndEntry> entries = f.getEntries();
            //Try setting Feed Author
            _feed.setAuthor(f.getAuthor());

            StringBuilder content = new StringBuilder();
            for (SyndEntry c : entries) {
                //clear for reuse
                content = content.delete(0, content.length());

                RSSItem item = new RSSItem();
                item.setDate(c.getPublishedDate().toString());
                item.setTitle(c.getTitle());

                //If feed author is not yet set, use item authors
                if (_feed.getAuthor() == null || _feed.getAuthor().isEmpty()) {
                     _feed.setAuthor(c.getAuthor() + " and others");
                }

                List contents = c.getContents();
                if (!(contents == null || contents.isEmpty())) {
                    //Get Content
                    for (Object syndContent : contents) {
                        content.append(((SyndContent) syndContent).getValue());
                    }
                    item.setContent(content.toString());

                    //Try fetching image from content
                    String img = "";
                    String temp = content.toString().replaceAll("<", "&;").replaceAll(">", "&;");
                    for (String string : temp.split("&;")) {
                        if (!string.trim().matches("img[^<>]+/")) continue;
                        int icount = 0;
                        int i = string.indexOf("src");
                        if (i < 0) continue;

                        StringBuilder b = new StringBuilder();
                        for (; i < string.length(); ++i) {
                            if (string.charAt(i) == '\"') {
                                ++icount;
                                continue;
                            }
                            if (icount == 1) {
                                b.append(string.charAt(i));
                                continue;
                            }
                            if (icount > 1) break;
                        }
                        img = b.toString();
                        break;
                    }

                    item.setImage(img);
                }

                _feed.addItem(item);
            }

        } catch (FetcherException | FeedException | IOException e) {
            Log.d("Tatafo debug", "Failed: " + e.getMessage());
        }

        return _feed;

    }


}
