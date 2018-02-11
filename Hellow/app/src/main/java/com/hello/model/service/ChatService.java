package com.hello.model.service;

import com.hello.model.data.ChatData;
import com.hello.model.service.api.ChatApi;
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class ChatService {
    private final String url = "http://api.qingyunke.com/";
    private final ChatApi api;

    @Inject
    ChatService() {
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AppHttpLoggingInterceptor())
                .build();

        api = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ChatApi.class);
    }

    public Single<ChatData> getNews(String msg) {
        return api.getChat(msg);
    }
}
