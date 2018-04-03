package com.hello.model.service

import com.hello.model.data.KugoMusicData
import com.hello.model.service.api.KugoMusicApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class KugoMusicService @Inject constructor() {
    private val url = "http://www.kugou.com/"
    private val api: KugoMusicApi

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
                .create(KugoMusicApi::class.java)
    }

    fun getMusic(hash: String): Single<KugoMusicData> {
        return api.getMusic(hash)
    }
}