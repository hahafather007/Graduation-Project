package com.hello.model.data

import com.google.gson.annotations.SerializedName

data class CookData(val code: Int,
                    val text: String,
                    val list: List<CookItem>) {
    data class CookItem(val name: String,
                        val icon: String,
                        val info: String,
                        @SerializedName("detailurl") val url: String)
}