package com.hello.model.service;

import com.hello.model.data.NewsData;
import com.hello.model.service.api.NewApi;
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsService {
    private final String url = "http://v.juhe.cn/toutiao/";
    private final NewApi api;

    @Inject
    NewsService() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AppHttpLoggingInterceptor())
                .build();

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
                .map(value -> value.getResult().getNewsList());
    }
}
