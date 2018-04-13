package com.hello.viewmodel

import android.databinding.ObservableArrayList
import com.hello.common.RxController
import com.hello.model.data.HelpFunData
import com.hello.model.service.HelpService
import com.hello.utils.databinding.update
import com.hello.utils.rx.Singles
import javax.inject.Inject

class HelpViewModel @Inject constructor() : RxController() {
    val functions = ObservableArrayList<HelpFunData>()

    @Inject
    lateinit var helpService: HelpService

    @Inject
    fun init() {
        helpService.getFunctions()
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess { functions.update(it) }
                .subscribe()
    }
}