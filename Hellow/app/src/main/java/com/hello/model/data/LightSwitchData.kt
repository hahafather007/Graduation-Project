package com.hello.model.data

data class LightSwitchData(val state: State) {
    enum class State {
        ON,
        OFF
    }
}