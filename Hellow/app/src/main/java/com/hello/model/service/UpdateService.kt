package com.hello.model.service

import com.google.gson.Gson
import com.hello.model.data.UpdateInfoData
import com.hello.model.service.api.UpdateApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Inject

class UpdateService @Inject constructor() {
    private val url = "https://github.com/"
    private val api: UpdateApi

    init {
        val client = OkHttpClient.Builder()
                .addInterceptor(AppHttpLoggingInterceptor())
                .build()

        api = Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(UpdateApi::class.java)
    }

    fun getVersion(): Single<UpdateInfoData> {
        return api.getVersion()
                .map { Gson().fromJson(it.string(), UpdateInfoData::class.java) }
    }
}