package com.hello.model.service.api

import com.hello.model.data.KugoMusicData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface KugoMusicApi {
    @GET("yy/index.php?r=play/getdata")
    fun getMusic(@Query("hash") hash: String): Single<KugoMusicData>
}