package com.hello.viewmodel

import android.databinding.ObservableBoolean
import com.hello.common.RxController
import javax.inject.Inject

class SettingViewModel @Inject constructor() : RxController() {
    val loading = ObservableBoolean()

    @Inject
    fun init() {

    }

    //保存更改的信息
    fun save() {

    }
}