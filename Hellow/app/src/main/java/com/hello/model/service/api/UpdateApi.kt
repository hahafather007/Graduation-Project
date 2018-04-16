package com.hello.model.service.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface UpdateApi {
    @GET("hahafather007/Download/raw/master/哈喽助手/version.txt")
    fun getVersion(): Single<ResponseBody>
}