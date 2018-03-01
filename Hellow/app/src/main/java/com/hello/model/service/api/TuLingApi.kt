package com.hello.model.service.api

import com.hello.model.data.TuLingData
import com.hello.model.data.TuLingSendData
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface TuLingApi {
    @POST("/openapi/api/v2")
    fun getResult(@Body body: TuLingSendData): Single<TuLingData>
}