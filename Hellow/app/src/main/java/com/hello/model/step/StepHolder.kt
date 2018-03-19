package com.hello.model.step

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_COUNTER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.hello.utils.Log
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepHolder @Inject constructor() {
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
    }

    //转换步数的准确值
    private fun calculateStep(count: Int) {
        step.onNext(count)
    }
}