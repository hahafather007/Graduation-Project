package com.hello.model.service

import com.hello.model.data.CookData
import com.hello.model.data.TuLingData
import com.hello.model.data.TuLingSendData
import com.hello.model.service.api.TuLingApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TuLingService @Inject constructor() {
    private val url = "http://www.tuling123.com/"
    private val api: TuLingApi

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
                .create(TuLingApi::class.java)
    }

    fun getResult(body: TuLingSendData): Single<TuLingData> {
        return api.getResult(body)
    }

    fun getCook(body: TuLingSendData): Single<CookData> {
        return api.getCook(body)
    }
}
