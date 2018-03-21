package com.hello.model.pref

import com.chibatching.kotpref.KotprefModel
import com.hello.common.Constants
import com.hello.common.SpeechPeople.*

object HelloPref : KotprefModel() {
    override val kotprefName = Constants.KOTPREF_NAME

    //语音合成发音人
    var talkPeople: String by stringPref(default = XIAO_QI)
    //计步信息
    var stepCount: Int by intPref(default = 0)
}