package com.hello.model.data

import com.google.gson.annotations.SerializedName

data class BaiduParam(
        @SerializedName("accept-audio-data")
        val acceptData: Boolean = false,
        @SerializedName("disable-punctuation")
        val disablePunction: Boolean = false,
        @SerializedName("accept-audio-volume")
        val acceptVolume: Boolean = true,
        val pid: Int = 1736)