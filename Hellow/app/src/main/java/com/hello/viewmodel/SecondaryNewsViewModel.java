package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import com.hello.common.RxController;
import com.hello.model.data.NewsData;
import com.hello.model.service.NewsService;
import com.hello.utils.rx.Singles;

import java.util.List;

import javax.inject.Inject;

public class SecondaryNewsViewModel extends RxController {
    public ObservableList<NewsData> newsList = new ObservableArrayList<>();
    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableBoolean newsEnd = new ObservableBoolean();

    //因为api服务器不支持加载更多，所以本地缓存实现假加载更多
    private List<NewsData> newsHolder;
    //用来标记是否为新加载的数据
    private boolean newData;

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
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(v -> {
                    newData = true;
                    newsList.clear();
                    newsList.addAll(v.subList(0, 8));
                    newsEnd.set(false);
                    newsHolder = v;
                })
                .subscribe();
    }

    public void refresh() {
        if (!loading.get()) {
            init();
        }
    }

    public void loadMore() {
        if (newsList.size() < newsHolder.size()) {
            newData = false;
            if (newsHolder.size() >= newsList.size() + 8) {
                newsList.addAll(newsHolder.subList(newsList.size(), newsList.size() + 8));
            } else {
                newsList.addAll(newsHolder.subList(newsList.size(), newsHolder.size()));
                newsEnd.set(true);
            }
        }
    }

    public boolean isNewData() {
        return newData;
    }
}
