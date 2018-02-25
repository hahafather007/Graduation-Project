package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import com.hello.model.data.NewsData;
import com.hello.model.service.NewsService;
import com.hello.utils.Log;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

public class SecondaryNewsViewModel {
    public ObservableList<NewsData> newsList = new ObservableArrayList<>();
    public ObservableBoolean loading = new ObservableBoolean();

    @Inject
    NewsService newsService;

    @Inject
    SecondaryNewsViewModel() {
    }

    @Inject
    void init() {
        newsService.getNews()
                .compose(Singles.async())
                .compose(Singles.status(loading))
                .subscribe(value -> {
                    newsList.clear();
                    newsList.addAll(value);
                }, Log::e);
    }

    public void refresh() {
        if (!loading.get()) {
            init();
        }
    }
}
