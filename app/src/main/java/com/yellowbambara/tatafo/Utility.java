package com.yellowbambara.tatafo;

import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.FetcherException;
import com.google.code.rome.android.repackaged.com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.yellowbambara.tatafo.parser.DOMParser;

import org.jdom.adapters.XML4JDOMAdapter;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Admin on 04/07/2015.
 */
public class Utility {

    public static String stripFeedTags(String value){
        StringBuilder b = new StringBuilder();
        int length = value.length();
        boolean inTag = false;
        for (int i = 0; i < length; i++){
            char c = value.charAt(i);
            if(c == '<'){
                inTag = true;
                continue;
            }else if (c == '>'){
                inTag = false;
                continue;
            }

            if(!inTag){
                b.append(c);
            }
        }

        return b.toString();
    }
}
