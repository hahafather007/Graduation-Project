package com.hello.model.service.api

import io.reactivex.Completable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BackupApi {
    @Multipart
    @POST("backup/note")
    fun backupNote(@Part("description") description: RequestBody,
                   @Part body: MultipartBody.Part): Completable
}