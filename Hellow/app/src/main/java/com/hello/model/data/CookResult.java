package com.hello.model.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CookResult {
    private Answer answer;
    private Data data;

    public Answer getAnswer() {
        return answer;
    }

    public Data getData() {
        return data;
    }

    public class Result {
        @SerializedName("imgUrl")
        private String image;
        private String ingredient;
        private String steps;

        public String getImage() {
            return image;
        }

        public String getIngredient() {
            return ingredient;
        }

        public String getSteps() {
            return steps;
        }
    }

    public class Answer {
        private String text;

        public String getText() {
            return text;
        }
    }

    public class Data {
        private List<Result> result;

        public List<Result> getResult() {
            return result;
        }
    }
}
