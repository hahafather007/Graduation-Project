package com.hello.model.service.api;

import com.hello.model.data.response.NewsReponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewApi {
    @GET(value = "index?type=top&key=c116bf742a3fa1f619a4632b1059c051")
    Single<NewsReponse> getNews();
}
