package com.hello.model.data.response;


import com.google.gson.annotations.SerializedName;
import com.hello.model.data.NewsData;

import java.util.List;

public class NewsReponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("data")
        private List<NewsData> newsList;

        public List<NewsData> getNewsList() {
            return newsList;
        }
    }
}
