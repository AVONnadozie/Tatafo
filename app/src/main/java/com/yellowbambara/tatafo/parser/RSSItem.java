package com.yellowbambara.tatafo.parser;

import com.yellowbambara.tatafo.Utility;

import java.io.Serializable;
import java.util.Date;

public class RSSItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title = null;
    private String content = null;
    private Date date = null;
    private String image = null;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setContent(String content) {
        if (content == null || content.equals("null"))
            this.content = "";
        else
            this.content = content;
    }

    void setDate(Date pubdate) {
        date = pubdate;
    }

    void setImageURL(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return getFriendlyDate(date);
    }

    public String getImage() {
        return image;
    }

    private String getFriendlyDate(Date date) {
        return Utility.getFriendlyDate(date);
    }

}
