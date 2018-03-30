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
    //助手的性别，0表示妹子，1表示帅锅
    var helloSex: Int by intPref(default = 0)
}