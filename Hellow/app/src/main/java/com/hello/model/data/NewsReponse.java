package com.hello.model.data;


import com.google.gson.annotations.SerializedName;
import com.hello.model.data.NewsData;

import java.util.List;

public class NewsReponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public static class Result {
        @SerializedName("data")
        private List<NewsData> newsList;

        public List<NewsData> getNewsList() {
            return newsList;
        }
    }
}
