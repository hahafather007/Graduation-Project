package com.hello.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableInt
import android.databinding.ObservableList
import com.hello.model.db.table.StepInfo
import com.hello.model.step.StepHolder
import com.hello.utils.rx.Singles
import javax.inject.Inject

class SportViewModel @Inject constructor() {
    val stepInfoes: ObservableList<StepInfo> = ObservableArrayList()
    val step = ObservableInt()

    @Inject
    lateinit var stepHolder: StepHolder

    @Inject
    fun init() {
        stepHolder.step
                .take(1)
                .subscribe { step.set(it) }
    }

    fun initStepInfoes() {
        stepHolder.getStepInfoes()
                .compose(Singles.async())
                .subscribe { v -> stepInfoes.addAll(v) }
    }
}