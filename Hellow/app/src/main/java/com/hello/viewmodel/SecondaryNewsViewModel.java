package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.hello.model.data.NewsData;
import com.hello.model.service.NewsService;
import com.hello.utils.Log;
import com.hello.utils.rx.Singles;

import java.util.List;

import javax.inject.Inject;

public class SecondaryNewsViewModel {
    public ObservableList<List<NewsData>> newsList = new ObservableArrayList<>();

    @Inject
    NewsService newsService;

    @Inject
    SecondaryNewsViewModel() {
    }

    @Inject
    void init() {
        newsService.getNews()
                .compose(Singles.async())
                .subscribe(newsList::add, Log::e);
    }
}
