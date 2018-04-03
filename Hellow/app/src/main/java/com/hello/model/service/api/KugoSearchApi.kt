package com.hello.model.service.api

import com.hello.model.data.KugoSearchData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface KugoSearchApi {
    @GET("song_search_v2")
    fun getMusicList(@Query("keyword") name: String,
                     @Query("page") page: Int = 1,
                     @Query("pagesize") pageSize: Int = 1,
                     @Query("userid") userId: Int = -1): Single<KugoSearchData>
}