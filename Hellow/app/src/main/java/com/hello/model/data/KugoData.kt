package com.hello.model.data

import com.google.gson.annotations.SerializedName

data class KugoSearchData(val status: Int,
                          val data: Data) {

    data class Data(
            val page: Int,
            val lists: List<Music>) {

        data class Music(
                @SerializedName("SingerName")
                val singer: String?,
                @SerializedName("FileName")
                val name: String?,
                @SerializedName("MvHash")
                val mvHash: String?,
                @SerializedName("FileHash")
                val hash: String?)

    }
}

data class KugoMusicData(val status: Int,
                         val data: Data) {

    data class Data(val img: String,
                    @SerializedName("author_name")
                    val singer: String,
                    @SerializedName("audio_name")
                    val name: String,
                    @SerializedName("play_url")
                    val url: String)
}