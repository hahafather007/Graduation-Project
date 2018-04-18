package com.hello.common

//语音唤醒模式
class WakeUpMode {
    companion object {
        const val ORDER = "order"//命令式：如"小哈同学，打开电筒"
        const val CALL = "call"//应答式：喊出"小哈同学"，回答之后才说出命令
    }
}