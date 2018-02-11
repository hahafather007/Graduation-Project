package com.hello.model.service.api

import com.hello.model.data.ChatData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {
    @GET("api.php?key=free&appid=0")
    fun getChat(@Query("msg") msg: String): Single<ChatData>
}