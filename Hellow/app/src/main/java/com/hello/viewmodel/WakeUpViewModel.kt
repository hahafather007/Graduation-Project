package com.hello.viewmodel

import com.annimon.stream.Optional
import com.hello.common.RxController
import com.hello.model.aiui.WakeUpHolder
import com.hello.utils.rx.Observables
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class WakeUpViewModel @Inject constructor() : RxController() {
    var error: Subject<Optional<*>> = PublishSubject.create()

    @Inject
    lateinit var wakeUpHolder: WakeUpHolder

    @Inject
    fun init() {
        wakeUpHolder.error
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { error.onNext(Optional.empty<Any>()) }
                .subscribe()
    }

    fun startListening() {
        wakeUpHolder.startListening()
    }

    fun stopListening() {
        wakeUpHolder.stopListening()
    }

    override fun onCleared() {
        super.onCleared()

        wakeUpHolder.onCleared()
    }
}