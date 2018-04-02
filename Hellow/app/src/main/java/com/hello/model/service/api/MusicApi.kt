package com.hello.model.service.api

import com.hello.model.data.QQMusicData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApi {
    @GET("fcgi-bin/music_search_new_platform?t=0&n=1&aggr=1&cr=1&loginUin=0&format=json" +
            "&inCharset=GB2312&outCharset=utf-8&notice=0&platform=jqminiframe.json&needNewCode=0" +
            "&p=1&catZhida=0&remoteplace=sizer.newclient.next_song")
    fun getMusicList(@Query("w") name: String): Single<QQMusicData>
}