package com.hello.model.service

import com.hello.model.db.table.Note
import com.hello.model.service.api.BackupApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Inject

class BackupService @Inject constructor() {
    private val url = "http://xxx.xxx.xxx.xxx/"
    private val api: BackupApi

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(AppHttpLoggingInterceptor())
                .build()

        api = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BackupApi::class.java)
    }

    fun synchronizeNote(notes: List<Note>): Completable {
        return api.synchronizeNote(notes)
    }

    fun backupNote(description: RequestBody, body: MultipartBody.Part): Completable {
        return api.backupNote(description, body)
    }

    fun restoreNote(): Single<List<Note>> {
        return api.restoreNote()
    }

    fun restoreFile(fileName: String): Single<ResponseBody> {
        //TODO 下载云端备份的录音文件
        return Single.just(ResponseBody.create(null, ""))
    }
}