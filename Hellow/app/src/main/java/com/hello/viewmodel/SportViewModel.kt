package com.hello.viewmodel

import android.databinding.ObservableInt
import com.hello.model.step.StepHolder
import javax.inject.Inject

class SportViewModel @Inject constructor() {
    val step = ObservableInt()

    @Inject
    lateinit var stepHolder: StepHolder

    @Inject
    fun init() {
        stepHolder.step
                .take(1)
                .subscribe { step.set(it) }
    }
}