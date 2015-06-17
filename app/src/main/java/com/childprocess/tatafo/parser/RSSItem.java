package com.childprocess.tatafo.parser;

import java.io.Serializable;

public class RSSItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String _title = null;
    private String _content = null;
    private String _date = null;
    private String _image = null;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    void setTitle(String title) {
        _title = title;
    }

    void setContent(String content) {
        _content = content;
    }

    void setDate(String pubdate) {
        _date = pubdate;
    }

    void setImage(String image) {
        _image = image;
    }

    public String getTitle() {
        return _title;
    }

    public String getContent() {
        return _content;
    }

    public String getDate() {
        return _date;
    }

    public String getImage() {
        return _image;
    }

}
