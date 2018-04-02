package com.hello.model.service

import com.hello.model.data.QQMusicData
import com.hello.model.service.api.MusicApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class MusicService @Inject constructor() {
    private val url = "http://s.music.qq.com/"
    private val api: MusicApi

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
                .create(MusicApi::class.java)
    }

    fun getMusicList(num: Int, name: String): Single<QQMusicData> {
        return api.getMusicList(name)
    }
}