package com.hello.model.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JokeResult {
    @SerializedName("result")
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public class Result {
        private String category;
        private String content;
        private String title;

        public String getCategory() {
            return category;
        }

        public String getContent() {
            return content;
        }

        public String getTitle() {
            return title;
        }
    }
}
