package com.yellowbambara.tatafo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yellowbambara.tatafo.R;
import com.yellowbambara.tatafo.parser.RSSItem;

public class DetailFragment extends Fragment {

    private int fPos;
    private RSSItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (RSSItem) getArguments().getSerializable("feed_item");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        // Initializer views
        TextView title = (TextView) view.findViewById(R.id.title);
        WebView desc = (WebView) view.findViewById(R.id.desc);
        // Enable the vertical fading edge (by default it is disabled)
        ScrollView sv = (ScrollView) view.findViewById(R.id.sv);
        sv.setVerticalFadingEdgeEnabled(true);
        // Set webview properties
        WebSettings ws = desc.getSettings();
        ws.setLightTouchEnabled(false);
        ws.setPluginState(PluginState.ON);
        ws.setJavaScriptEnabled(true);

        //MODIFIED
        ws.setMinimumFontSize(20);

        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setBuiltInZoomControls(true);
        ws.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        // Set the views
        title.setText(item.getTitle());
        desc.loadData(item.getContent(), "text/html", "UTF-8");
        return view;
    }
}
