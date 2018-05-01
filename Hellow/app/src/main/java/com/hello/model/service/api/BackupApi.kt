package com.hello.model.service.api

import com.hello.model.data.NoteListData
import com.hello.model.db.table.Note
import com.hello.model.pref.HelloPref
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface BackupApi {
    @POST("hello/backup/note")
    fun backupNote(@Query("openId") openId: String = HelloPref.openId
            ?: "", @Body notes: NoteListData): Completable

    @GET("hello/restore/note")
    fun restoreNote(@Query("openId") openId: String = HelloPref.openId
            ?: ""): Single<List<Note>>
}