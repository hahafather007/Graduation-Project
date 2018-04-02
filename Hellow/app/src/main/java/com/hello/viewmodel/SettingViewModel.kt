package com.hello.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.hello.common.RxController
import com.hello.common.SpeechPeople.XIAO_YAN
import com.hello.common.SpeechPeople.XIAO_YU
import com.hello.model.pref.HelloPref
import com.hello.utils.rx.Singles
import com.hello.viewmodel.SettingViewModel.HelloSex.BOY
import com.hello.viewmodel.SettingViewModel.HelloSex.GIRL
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SettingViewModel @Inject constructor() : RxController() {
    val loading = ObservableBoolean()
    val helloSex = ObservableField<HelloSex>()
    val autoBackup = ObservableBoolean()
    val canWakeup = ObservableBoolean()

    val success: Subject<Boolean> = PublishSubject.create()

    @Inject
    fun init() {
        //判断当前选择的性别
        if (HelloPref.talkPeople == XIAO_YAN) {
            helloSex.set(GIRL)
        } else {
            helloSex.set(BOY)
        }

        autoBackup.set(HelloPref.isAutoBackup)
        canWakeup.set(HelloPref.isCanWakeup)
    }

    //保存更改的信息
    fun save() {
        Single.timer(1, TimeUnit.SECONDS)
                .compose(Singles.async())
                .compose(Singles.status(loading))
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess {
                    HelloPref.talkPeople = if (helloSex.get() == GIRL) XIAO_YAN else XIAO_YU
                    HelloPref.isAutoBackup = autoBackup.get()
                    HelloPref.isCanWakeup = canWakeup.get()

                    success.onNext(true)
                }
                .doOnError { success.onNext(false) }
                .subscribe()
    }

    enum class HelloSex {
        GIRL,
        BOY
    }
}