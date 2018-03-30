package com.hello.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.hello.common.RxController
import com.hello.model.pref.HelloPref
import javax.inject.Inject

class SettingViewModel @Inject constructor() : RxController() {
    val loading = ObservableBoolean()
    val helloSex = ObservableField<HelloSex>()

    @Inject
    fun init() {
        if (HelloPref.helloSex == 0) {
            helloSex.set(HelloSex.GIRL)
        } else {
            helloSex.set(HelloSex.BOY)
        }
    }

    //保存更改的信息
    fun save() {

    }

    enum class HelloSex {
        GIRL,
        BOY
    }
}