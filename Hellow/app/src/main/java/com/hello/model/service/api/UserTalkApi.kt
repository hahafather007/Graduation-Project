package com.hello.model.service.api

import com.hello.model.data.SpeakListData
import io.reactivex.Completable
import retrofit2.http.Body
import retrofit2.http.POST

interface UserTalkApi {
    @POST("hello/talk/upload")
    fun uploadData(@Body notes: SpeakListData): Completable
}