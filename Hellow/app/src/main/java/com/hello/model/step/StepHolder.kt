package com.hello.model.step

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_COUNTER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.annimon.stream.Optional
import com.hello.common.Constants.DATA_FORMAT
import com.hello.common.RxController
import com.hello.model.db.table.StepInfo
import com.hello.model.pref.HelloPref
import com.hello.utils.Log
import com.hello.utils.SystemTimeUtil
import com.hello.utils.isListValid
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles
import com.raizlabs.android.dbflow.sql.language.Select
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.joda.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepHolder @Inject constructor() : RxController() {
    //系统开机时间
    private val powerUpTime = SystemTimeUtil.getPowerUpTime()
    private var stepCount: Int = 0
    //用于记录第一次数据
    private var firstSaveStep: Boolean = false
    private var cacheStepInfo: List<StepInfo> = emptyList()

    val step: Subject<Int> = PublishSubject.create()

    @Inject
    lateinit var context: Context

    private lateinit var manager: SensorManager
    private lateinit var sensorListener: SensorEventListener

    @Inject
    fun init() {
        manager = context.getSystemService(SENSOR_SERVICE) as SensorManager

        sensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                Log.i(event!!.values[0])
                calculateStep(event.values[0].toInt())
            }
        }

        getStepInfoes().compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess {
                    manager.registerListener(sensorListener,
                            manager.getDefaultSensor(TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_UI)

                    saveStepInfo()
                }
                .subscribe()
    }

    //防止内存泄漏
    override fun onCleared() {
        compositeDisposable.clear()

        manager.unregisterListener(sensorListener)
    }

    //获得所有计步信息
    fun getStepInfoes(): Single<List<StepInfo>> {
        return if (isListValid(cacheStepInfo)) {
            Single.just(cacheStepInfo)
        } else {
            Single.just(Select().from(StepInfo::class.java).queryList())
                    .map { it.sortedBy { it.time } }
                    .doOnSuccess { cacheStepInfo = it }
        }
    }

    private fun saveStepInfo() {
        Observable.interval(30, TimeUnit.SECONDS)
                .map { cacheStepInfo.toMutableList() }
                .flatMap {
                    //若果系统开机时间等于当前时间，则准确步数等于传感器数据
                    val step = if (powerUpTime == LocalDate.now()) {
                        stepCount
                    } else {//若开机时间是昨天，则步数等于传感器数据减去昨天的
                        stepCount - HelloPref.stepCount
                    }

                    it.forEach { info ->
                        if (info.time == LocalDate.now().toString(DATA_FORMAT)) {
                            info.stepCount = step
                            return@flatMap Observable.just(info.save())
                                    .map { _ -> it }
                        }
                    }

                    val stepInfo = StepInfo(LocalDate.now().toString(DATA_FORMAT), step)
                    return@flatMap Observable.just(stepInfo.save())
                            .map { _ -> it.add(stepInfo) }
                            .map { _ -> it }
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { cacheStepInfo = it }
                .subscribe()
    }

    //转换步数的准确值
    private fun calculateStep(count: Int) {
        //若果系统开机时间等于当前时间，则准确步数等于传感器数据
        stepCount = if (powerUpTime == LocalDate.now()) {
            count
        } else {//若开机时间是昨天，则步数等于传感器数据减去昨天的
            count - HelloPref.stepCount
        }

        if (!firstSaveStep) {
            HelloPref.stepCount = stepCount
            firstSaveStep = true

            Observable.just(cacheStepInfo.toMutableList())
                    .flatMap {
                        it.forEach { info ->
                            if (info.time == LocalDate.now().toString(DATA_FORMAT)) {
                                info.stepCount = stepCount
                                return@flatMap Observable.just(info.save())
                                        .map { _ -> it }
                            }
                        }

                        val stepInfo = StepInfo(LocalDate.now().toString(DATA_FORMAT), stepCount)
                        return@flatMap Observable.just(stepInfo.save())
                                .map { _ -> it.add(stepInfo) }
                                .map { _ -> it }
                    }
                    .compose(Observables.async())
                    .compose(Observables.disposable(compositeDisposable))
                    .doOnNext { cacheStepInfo = it }
                    .subscribe()
        }

        step.onNext(stepCount)
    }
}