package com.hello.model.baidu

import android.content.Context
import com.annimon.stream.Optional
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant.*
import com.google.gson.Gson
import com.hello.utils.Log
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class VoiceHolder @Inject constructor() {
    //出错的回调
    val error: Subject<Optional<*>> = PublishSubject.create()
    //得到返回结果的回调
    val result: Subject<String> = PublishSubject.create()
    //表示每一局的分隔
    val part: Subject<Optional<*>> = PublishSubject.create()
    //声音分贝
    val decibel: Subject<Int> = PublishSubject.create()

    lateinit var manager: EventManager

    @Inject
    lateinit var context: Context

    @Inject
    fun init() {
        manager = EventManagerFactory.create(context, "asr")
        manager.registerListener { name, params, data, offset, length ->
            when (name) {
                CALLBACK_EVENT_ASR_READY -> Log.i("百度语音：就绪！！！")
                CALLBACK_EVENT_ASR_FINISH -> {
                    Log.i("百度语音：识别结束！！！")
                    part.onNext(Optional.empty<Any>())
                }
                CALLBACK_EVENT_ASR_ERROR -> {
                    Log.e("百度语音：错误！！！")
                    error.onNext(Optional.empty<Any>())
                }
                CALLBACK_EVENT_ASR_PARTIAL -> {
                    try {
                        val text = JSONObject(params).getJSONArray("results_recognition").getString(0)
                        if (text != null) {
                            result.onNext(text)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                CALLBACK_EVENT_ASR_VOLUME -> {
                    Log.i(params)
                    decibel.onNext(JSONObject(params).getInt("volume"))
                }
            }
        }
    }

    fun startRecord() {
        val params = HashMap<String, Any>()
        params[ACCEPT_AUDIO_DATA] = true
        params[ACCEPT_AUDIO_VOLUME] = true
        params[VAD_ENDPOINT_TIMEOUT] = 0

        val paramJson = Gson().toJson(params)
        Log.i(paramJson)

        //ASR_STARTS开始事件参数
        manager.send(ASR_START, paramJson, null, 0, 0)
    }

    //结束当前录音，有返回结果
    fun stopRecord() {
        manager.send(ASR_STOP, null, null, 0, 0)
    }

    //取消当前录音，无返回结果
    fun cancelRecord() {
        manager.send(ASR_CANCEL, null, null, 0, 0)
    }
}