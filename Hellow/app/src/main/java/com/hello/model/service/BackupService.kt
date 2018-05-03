package com.hello.model.service

import com.hello.model.data.NoteListData
import com.hello.model.db.table.Note
import com.hello.model.service.api.BackupApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupService @Inject constructor() {
    private val url = "http://47.106.86.50:8080/"
    private val api: BackupApi

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(AppHttpLoggingInterceptor())
                .build()

        api = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BackupApi::class.java)
    }

    fun backupNote(notes: List<Note>): Completable {
        return api.backupNote(notes = NoteListData(notes))
    }

    fun restoreNote(): Single<List<Note>> {
        return api.restoreNote()
                .map { it.notes }
    }

    fun restoreFile(fileName: String): Single<ResponseBody> {
        //TODO 下载云端备份的录音文件
        return Single.just(ResponseBody.create(null, ""))
    }
}