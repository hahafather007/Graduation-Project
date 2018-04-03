package com.hello.model.service

import com.hello.model.data.KugoSearchData
import com.hello.model.service.api.KugoSearchApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class KugoSearchService @Inject constructor() {
    private val url = "http://songsearch.kugou.com/"
    private val api: KugoSearchApi

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
                .create(KugoSearchApi::class.java)
    }

    fun getMusicList(num: Int, name: String): Single<KugoSearchData> {
        return api.getMusicList(name = name, pageSize = num)
    }
}