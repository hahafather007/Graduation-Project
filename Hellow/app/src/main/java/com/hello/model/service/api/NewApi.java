package com.hello.model.service.api;

import com.hello.model.data.response.NewsReponse;

import io.reactivex.Single;
import retrofit2.http.GET;

import static com.hello.common.Constants.*;

public interface NewApi {
    @GET(value = "index?type=top&key=" + NEWS_KEY)
    Single<NewsReponse> getNews();
}
