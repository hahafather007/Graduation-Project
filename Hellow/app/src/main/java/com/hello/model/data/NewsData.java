package com.hello.model.data;

import com.google.gson.annotations.SerializedName;

//新闻头条的数据类
public class NewsData {
    private String title;
    private String date;
    @SerializedName("author_name")
    private String author;
    private String url;
    @SerializedName("thumbnail_pic_s")
    private String imagel;

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getImagel() {
        return imagel;
    }
}
