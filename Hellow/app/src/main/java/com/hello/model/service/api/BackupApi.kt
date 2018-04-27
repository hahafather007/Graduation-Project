package com.hello.model.service.api

import com.hello.model.db.table.Note
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface BackupApi {
    @Multipart
    @POST("backup/note")
    fun backupNote(@Part("description") description: RequestBody,
                   @Part body: MultipartBody.Part): Completable

    @GET("restore/note")
    fun restoreNote(): Single<List<Note>>

    @POST("synchronize/note")
    fun synchronizeNote(@Body notes:List<Note>): Completable
}