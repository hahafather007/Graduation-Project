package com.hello.model.pref

import com.chibatching.kotpref.KotprefModel
import com.hello.common.Constants
import com.hello.common.SpeechPeople.*

class HelloPref : KotprefModel() {
    override val kotprefName = Constants.KOTPREF_NAME

    var talkPeople: String by stringPref(default = XIAO_QI)
}