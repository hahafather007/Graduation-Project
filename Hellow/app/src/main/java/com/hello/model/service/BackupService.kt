package com.hello.model.service

import com.hello.model.service.api.BackupApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Completable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
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

    fun backupNote(file: MultipartBody.Part): Completable {
        return api.backupNote(file)
    }
}