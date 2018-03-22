package com.hello.model.step

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_COUNTER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.hello.model.db.table.StepInfo
import com.hello.model.pref.HelloPref
import com.hello.utils.Log
import com.hello.utils.SystemTimeUtil
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

class StepHolder @Inject constructor() {
    //系统开机时间
    private val powerUpTime = SystemTimeUtil.getPowerUpTime()
    private var stepCount: Int = 0

    val step: Subject<Int> = PublishSubject.create()

    @Inject
    lateinit var context: Context

    @Inject
    fun init() {
        val manager = context.getSystemService(SENSOR_SERVICE) as SensorManager
        val detector = manager.getDefaultSensor(TYPE_STEP_COUNTER)

        manager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent?) {
                Log.i(event!!.values[0])
                calculateStep(event.values[0].toInt())
            }
        }, detector, SensorManager.SENSOR_DELAY_UI)

        HelloPref.stepCount = stepCount

        Log.i(powerUpTime)
    }

    //获得所有计步信息
    fun getStepInfoes(): Single<List<StepInfo>> {
        return Single.just(Select().from(StepInfo::class.java).queryList())
                .map { it.sortedBy { it.time } }
                .doOnSuccess { saveStepInfo(it) }
    }

    private fun saveStepInfo(infoes: List<StepInfo>) {
        Observable.interval(30, TimeUnit.SECONDS)
                .flatMap {
                    for (info in infoes) {
                        if (info.time == LocalDate.now().toString()) {
                            info.stepCount = stepCount
                            return@flatMap Observable.just(info.save())
                        }
                    }
                    return@flatMap Observable.just(
                            StepInfo(LocalDate.now().toString(), stepCount).save())
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (powerUpTime == LocalDate.now()) {
                        HelloPref.stepCount = stepCount
                    }
                }
    }

    //转换步数的准确值
    private fun calculateStep(count: Int) {
        //若果系统开机时间等于当前时间，则准确步数等于传感器数据
        stepCount = if (powerUpTime == LocalDate.now()) {
            count
        } else {//若开机时间是昨天，则步数等于传感器数据减去昨天的
            count - HelloPref.stepCount
        }

        step.onNext(stepCount)
    }
}