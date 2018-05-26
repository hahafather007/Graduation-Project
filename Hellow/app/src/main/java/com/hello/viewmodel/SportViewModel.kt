package com.hello.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableInt
import android.databinding.ObservableList
import com.hello.common.Constants.DATA_FORMAT
import com.hello.common.RxController
import com.hello.model.db.table.StepInfo
import com.hello.model.pref.HelloPref
import com.hello.model.step.StepHolder
import com.hello.utils.Log
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles
import org.joda.time.LocalDate
import javax.inject.Inject

class SportViewModel @Inject constructor() : RxController() {
    val stepInfoes: ObservableList<StepInfo> = ObservableArrayList()
    val step = ObservableInt()

    @Inject
    lateinit var stepHolder: StepHolder

    @Inject
    fun init() {
        stepHolder.step
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { step.set(it) }
                .subscribe()

        stepHolder.getStepInfoes()
                .map {
                    val steps = it.take(8).toMutableList()

                    step.set(steps.lastOrNull()?.stepCount ?: 0)

                    steps.forEach { Log.i("${it.stepCount}   ") }
                    steps.remove(steps.last())

                    if (steps.size < 7) {
                        //当计步数据的数量不足7天时，用0来填充
                        var index = steps.size
                        while (index < 7) {
                            steps.add(0, StepInfo("", 0))
                            index++
                        }
                    }

                    return@map steps
                }
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess {
                    stepInfoes.clear()
                    stepInfoes.addAll(it)
                }
                .subscribe()
    }
}