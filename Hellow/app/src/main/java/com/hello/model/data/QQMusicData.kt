package com.hello.model.data

import com.google.gson.annotations.SerializedName

data class QQMusicData(val code: Int,
                       val data: Data?) {
    data class Data(val song: Song?) {
        data class Song(val list: List<Music>?) {
            data class Music(
                    @SerializedName("fsinger")
                    val singer: String?,
                    val lyric: String?,
                    val singerid: Int?)
        }
    }
}