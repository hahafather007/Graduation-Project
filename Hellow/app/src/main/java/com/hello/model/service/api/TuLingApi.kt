package com.hello.model.service.api

import com.hello.model.data.CookData
import com.hello.model.data.TuLingData
import com.hello.model.data.TuLingSendData
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface TuLingApi {
    @POST("/openapi/api")
    fun getResult(@Body body: TuLingSendData): Single<TuLingData>

    @POST("/openapi/api")
    fun getCook(@Body body: TuLingSendData): Single<CookData>
}