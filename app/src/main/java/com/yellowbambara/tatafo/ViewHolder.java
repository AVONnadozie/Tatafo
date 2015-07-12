package com.yellowbambara.tatafo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Admin on 06/07/2015.
 */
public class ViewHolder {

    private ImageView iv;
    private TextView tvTitle;
    private TextView tvDate;

    public ViewHolder(View view) {
        iv = (ImageView) view.findViewById(R.id.thumb);
        tvTitle = (TextView) view.findViewById(R.id.title);
        tvDate = (TextView) view.findViewById(R.id.date);
    }

    public ImageView getImageView() {
        return iv;
    }

    public TextView getTextViewTitle() {
        return tvTitle;
    }

    public TextView getTextViewDate() {
        return tvDate;
    }

}
