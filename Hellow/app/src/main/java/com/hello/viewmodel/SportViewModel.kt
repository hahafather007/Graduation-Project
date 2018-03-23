package com.hello.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableInt
import android.databinding.ObservableList
import com.hello.model.pref.HelloPref
import com.hello.model.step.StepHolder
import com.hello.utils.rx.Singles
import javax.inject.Inject

class SportViewModel @Inject constructor() {
    val stepInfoes: ObservableList<Int> = ObservableArrayList()
    val step = ObservableInt()

    @Inject
    lateinit var stepHolder: StepHolder

    @Inject
    fun init() {
        stepHolder.step
                .take(1)
                .subscribe { step.set(it) }

        stepHolder.stepInfoChange
                .subscribe { initStepInfoes() }
    }

    fun initStepInfoes() {
        stepHolder.getStepInfoes()
                .map {
                    val steps = it.map { it.stepCount }.take(7).toMutableList()

                    if (steps.size < 7) {
                        //当计步数据的数量不足7天时，用0来填充
                        var index = steps.size
                        while (index < 7) {
                            steps.add(0, 0)
                            index++
                        }

                    }
                    steps[6] = HelloPref.stepCount

                    return@map steps
                }
                .compose(Singles.async())
                .subscribe { v ->
                    stepInfoes.clear()
                    stepInfoes.addAll(v)
                }
    }
}