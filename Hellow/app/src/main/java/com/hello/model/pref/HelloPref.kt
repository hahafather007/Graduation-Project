package com.hello.model.pref

import com.chibatching.kotpref.KotprefModel
import com.hello.common.Constants
import com.hello.common.SpeechPeople.XIAO_YAN
import com.hello.common.WakeUpMode.Companion.CALL

object HelloPref : KotprefModel() {
    override val kotprefName = Constants.KOTPREF_NAME

    //是否自动备份
    var isAutoBackup: Boolean by booleanPref(default = false)
    //是否能后台唤醒
    var isCanWakeup: Boolean by booleanPref(default = false)
    //后台唤醒方式
    var wakeUpMode: String by stringPref(default = CALL)

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

    //当前的版本信息
    var versionInt: Int by intPref(default = 1)

    //已备份语音笔记id列表
    var noteBackupIds: String? by nullableStringPref(default = null)
}