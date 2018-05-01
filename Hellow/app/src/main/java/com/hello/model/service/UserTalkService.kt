package com.hello.model.service

import com.hello.model.data.SpeakListData
import com.hello.model.db.table.SpeakData
import com.hello.model.service.api.UserTalkApi
import com.hello.model.service.interceptor.AppHttpLoggingInterceptor
import io.reactivex.Completable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

//用来收集用户交互数据
class UserTalkService @Inject constructor() {
    private val url = "http://47.106.86.50:8080/"
    private val api: UserTalkApi

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
                .create(UserTalkApi::class.java)
    }

    fun uploadData(notes: List<SpeakData>): Completable {
        return api.uploadData(notes = SpeakListData(notes))
    }
}