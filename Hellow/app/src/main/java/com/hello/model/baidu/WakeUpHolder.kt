package com.hello.model.baidu

import android.content.Context
import com.annimon.stream.Optional
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant.*
import com.hello.common.RxController
import com.hello.model.aiui.AIUIHolder
import com.hello.utils.Log
import com.hello.utils.rx.Observables
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class WakeUpHolder @Inject constructor() : RxController() {
    var error: Subject<Optional<*>> = PublishSubject.create()
    var location: Subject<String> = PublishSubject.create()
    var result: Subject<Any> = PublishSubject.create()

    private lateinit var wakeup: EventManager
    private lateinit var params: String
    private lateinit var listener: EventListener

    @Inject
    lateinit var context: Context
    @Inject
    lateinit var aiuiHolder: AIUIHolder

    @Inject
    fun init() {
        wakeup = EventManagerFactory.create(context, "wp")

        listener = EventListener({ name, _, _, _, _ ->
            if ("wp.data" == name) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                try {
                    val code = JSONObject(params).getInt("errorCode")

                    //0表示唤醒成功
                    if (code == 0) {
                        val random = Math.random()
                        when (random) {
                            in 0.0..0.25 -> aiuiHolder.sendMessage("嗯")
                            in 0.25..0.5 -> aiuiHolder.sendMessage("怎么啦？")
                            in 0.5..0.75 -> aiuiHolder.sendMessage("什么？")
                            in 0.75..1.0 -> aiuiHolder.sendMessage("嗯嗯")
                        }

                        stopListening()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if ("wp.exit" == name) { // 唤醒已经停止
                Log.i("唤醒结束！！！")
            }
        })

        wakeup.registerListener(listener)

        val hashParams = HashMap<String, Any>()
        hashParams[WP_WORDS_FILE] = "assets:///wakeup.bin"
        hashParams[APP_ID] = 10887926
        params = JSONObject(hashParams).toString()

        addChangeListener()

        startListening()
    }

    fun startListening() {
        wakeup.send(WAKEUP_START, params, null, 0, 0)
    }

    fun stopListening() {
        wakeup.send(WAKEUP_STOP, null, null, 0, 0)
    }

    private fun addChangeListener() {
        aiuiHolder.speakOver
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { aiuiHolder.startRecording() }
                .subscribe()

        aiuiHolder.error
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { error.onNext(Optional.empty<Any>()) }
                .subscribe()

        aiuiHolder.location
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(location::onNext)
                .subscribe()

        aiuiHolder.aiuiResult
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    result::onNext
                    aiuiHolder.stopRecording()
                    startListening()
                }
                .subscribe()
    }

    override fun onCleared() {
        super.onCleared()

        wakeup.unregisterListener(listener)
    }
}