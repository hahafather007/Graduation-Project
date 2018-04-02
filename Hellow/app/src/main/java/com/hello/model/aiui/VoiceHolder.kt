package com.hello.model.aiui

import android.content.Context
import android.os.Bundle
import android.os.Environment
import com.hello.common.RxController
import com.hello.utils.Log
import com.hello.utils.SpeechJsonParser
import com.hello.utils.rx.Observables
import com.iflytek.cloud.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VoiceHolder @Inject constructor() : RxController() {
    //用于记录当前说活时长
    private var speakTime = 0
    //标记当前是否真正说话
    private var speaking = false

    //每句话识别的结果
    val resultText: Subject<String> = PublishSubject.create()
    //音量大小
    val volume: Subject<Int> = PublishSubject.create()

    private lateinit var mAsr: SpeechRecognizer
    //识别监听器
    private lateinit var listener: RecognizerListener

    @Inject
    lateinit var context: Context

    @Inject
    fun init() {
        mAsr = SpeechRecognizer.createRecognizer(context, {
            if (it != ErrorCode.SUCCESS) {
                Log.e("SpeechRecognizer初始化错误！！！（$it）")
            }
        })
        //设置识别引擎
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        //设置返回结果为json格式
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json")
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory().toString() + "/msc/asr.wav")

        listener = object : RecognizerListener {
            //data表示音频数据
            override fun onVolumeChanged(vol: Int, data: ByteArray?) {
                volume.onNext(vol)
                Log.i("音量：$vol")
            }

            override fun onResult(result: RecognizerResult?, isLast: Boolean) {
                val text = SpeechJsonParser.parseGrammarResult(result?.resultString)

                Log.i("识别结果：$text")
                resultText.onNext(text)

                if (speaking && speakTime >= 40) {
                    compositeDisposable.clear()
                    speakTime = 0

                    startRecording()
                }
            }

            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            override fun onBeginOfSpeech() {
                Log.i("我准备好了")
            }

            override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            }

            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入（最大只能识别60秒）
            override fun onEndOfSpeech() {
                Log.i("识别时间到")

                if (speaking) {
                    compositeDisposable.clear()
                    speakTime = 0

                    startRecording()
                }
            }

            override fun onError(error: SpeechError?) {
                Log.e("识别出错：${error?.errorCode}--->${error?.errorDescription}")
            }
        }
    }

    fun startRecording() {
        mAsr.startListening(listener)

        Observable.interval(40, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { speakTime++ }
                .subscribe()

        speaking = true
    }

    //停止识别，有返回结果
    fun stopRecording() {
        mAsr.stopListening()

        speaking = false
    }

    //取消识别，无返回结果
    fun cancelRecording() {
        mAsr.cancel()

        speaking = false
    }

    override fun onCleared() {
        super.onCleared()

        cancelRecording()
        mAsr.destroy()
    }
}