package com.hello.model.data;

public class JokeResult {
    private String category;
    private String content;
    private String title;

    public JokeResult(String category, String content, String title) {
        this.category = category;
        this.content = content;
        this.title = title;
    }

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
