package com.hello.model.data

data class MusicData(val url: String?,
                     val image: String?,
                     val name: String?,
                     val state: MusicState)

enum class MusicState {
    ON,
    OFF
}