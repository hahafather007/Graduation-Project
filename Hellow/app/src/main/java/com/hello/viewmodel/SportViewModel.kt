package com.hello.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableInt
import android.databinding.ObservableList
import com.hello.common.Constants
import com.hello.common.Constants.DATA_FORMAT
import com.hello.model.db.table.StepInfo
import com.hello.model.pref.HelloPref
import com.hello.model.step.StepHolder
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles
import org.joda.time.LocalDate
import javax.inject.Inject

class SportViewModel @Inject constructor() : ViewModel() {
    val stepInfoes: ObservableList<StepInfo> = ObservableArrayList()
    val step = ObservableInt()

    @Inject
    lateinit var stepHolder: StepHolder

    @Inject
    fun init() {
        stepHolder.step
                .take(1)
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { step.set(it) }
                .subscribe()

        stepHolder.stepInfoChange
                .compose(Observables.disposable(compositeDisposable))
                .subscribe { initStepInfoes() }
    }

    private fun initStepInfoes() {
        stepHolder.getStepInfoes()
                .map {
                    val steps = it.take(7).toMutableList()

                    if (steps.size < 7) {
                        //当计步数据的数量不足7天时，用0来填充
                        var index = steps.size
                        while (index < 7) {
                            steps.add(0, StepInfo("", 0))
                            index++
                        }
                    }

                    //确保表格当前的步数是准确的
                    if (steps[6].time == LocalDate.now().toString(DATA_FORMAT)) {
                        steps[6].stepCount = HelloPref.stepCount
                    } else {
                        steps.remove(steps[0])
                        steps.add(StepInfo(LocalDate.now().toString(DATA_FORMAT), HelloPref.stepCount))
                    }

                    return@map steps
                }
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess { v ->
                    stepInfoes.clear()
                    stepInfoes.addAll(v)
                }
                .subscribe()
    }
}