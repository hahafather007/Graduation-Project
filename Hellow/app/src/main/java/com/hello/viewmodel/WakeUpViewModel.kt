package com.hello.viewmodel

import com.hello.model.aiui.AIUIHolder
import com.hello.utils.Log
import javax.inject.Inject

class WakeUpViewModel @Inject constructor() {
    @Inject
    lateinit var aiuiHolder: AIUIHolder

    @Inject
    fun init() {
        Log.i("WakeUpViewModel初始化")
    }
}