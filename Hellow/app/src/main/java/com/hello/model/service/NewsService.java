package com.hello.model.service;

import com.hello.model.data.NewsData;
import com.hello.model.service.api.NewApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hello.common.Constants.NEWS_KEY;

@Singleton
public class NewsService {
    private final String url = "http://v.juhe.cn/toutiao/index/";
    private final String requestType = "top";
    private final NewApi api;

    @Inject
    NewsService() {
        final OkHttpClient client = new OkHttpClient.Builder().build();

        api = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewApi.class);
    }

    public Single<List<NewsData>> getNews() {
        return api.getNews()
                .map(value -> value.getData().getNewsList());
    }
}
