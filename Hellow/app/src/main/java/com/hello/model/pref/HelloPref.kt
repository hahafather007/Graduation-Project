package com.hello.model.pref

import com.chibatching.kotpref.KotprefModel
import com.hello.common.Constants
import com.hello.common.SpeechPeople.*

object HelloPref : KotprefModel() {
    override val kotprefName = Constants.KOTPREF_NAME

    //语音合成发音人
    var talkPeople: String by stringPref(default = XIAO_YAN)
    //计步信息
    var stepCount: Int by intPref(default = 0)
    //QQ openId（用户唯一标识）
    var openId: String? by nullableStringPref(default = null)
    //QQ登录的token
    var token: String? by nullableStringPref(default = null)
    var expires: String? by nullableStringPref(default = null)
    //QQ昵称
    var name: String? by nullableStringPref(default = null)
    //QQ头像地址
    var image: String? by nullableStringPref(default = null)
    //标记是否登录
    var isLogin: Boolean by booleanPref(default = false)
}