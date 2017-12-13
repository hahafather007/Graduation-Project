package com.hello.model.service.api;

import com.hello.model.data.response.NewsReponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewApi {
    @GET("/")
    Single<NewsReponse> getNews(@Query("type") String type, @Query("key") String key);
}
