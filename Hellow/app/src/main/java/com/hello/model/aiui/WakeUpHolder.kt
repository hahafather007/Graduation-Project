package com.hello.model.aiui

import android.content.Context
import android.os.Bundle
import com.annimon.stream.Optional
import com.hello.common.RxController
import com.hello.common.WakeUpMode.Companion.CALL
import com.hello.model.data.MusicData
import com.hello.model.pref.HelloPref
import com.hello.utils.Log
import com.hello.utils.SpeechJsonParser
import com.hello.utils.isStrValid
import com.hello.utils.rx.Observables
import com.iflytek.cloud.*
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class WakeUpHolder @Inject constructor() : RxController() {
    private lateinit var mAsr: SpeechRecognizer
    //识别监听器
    private lateinit var listener: RecognizerListener
    private var speaking = false
    //标记是否准备好应答用户的话
    private var readyToDo = false
    private var autoListening = false

    var error: Subject<Optional<*>> = PublishSubject.create()
    var location: Subject<String> = PublishSubject.create()
    var result: Subject<Any> = PublishSubject.create()
    var music: Subject<MusicData> = PublishSubject.create()

    @Inject
    lateinit var aiuiHolder: AIUIHolder
    @Inject
    lateinit var context: Context

    @Inject
    fun init() {
        mAsr = SpeechRecognizer.createRecognizer(context, null)
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat")
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin")

        listener = object : RecognizerListener {
            //data表示音频数据
            //音量值0~30
            override fun onVolumeChanged(vol: Int, data: ByteArray?) {
            }

            override fun onResult(result: RecognizerResult?, isLast: Boolean) {
                var text = SpeechJsonParser.parseGrammarResult(result?.resultString)
                        .replace("，", "")

                if (speaking) return

                //表示用户说了唤醒词
                if (text.contains("小哈同学") || text.contains("小哈助手")
                        || text.contains("哈喽助手")) {
                    speaking = true

                    //应答式唤醒模式
                    if (HelloPref.wakeUpMode == CALL) {
                        val random = Math.random()

                        when (random) {
                            in 0.0..0.2 -> aiuiHolder.speakText("嗯？")
                            in 0.2..0.4 -> aiuiHolder.speakText("怎么啦？")
                            in 0.4..0.6 -> aiuiHolder.speakText("什么？")
                            in 0.6..0.8 -> aiuiHolder.speakText("嗯嗯！")
                            in 0.8..1.0 -> aiuiHolder.speakText("干嘛？")
                        }

                        readyToDo = true

                        mAsr.stopListening()
                    } else {//命令式唤醒模式
                        text = when {
                            text.indexOf("小哈同学") != -1 ->
                                text.substring(text.indexOf("小哈同学") + 4)
                            text.indexOf("小哈助手") != -1 ->
                                text.substring(text.indexOf("小哈助手") + 4)
                            else ->
                                text.substring(text.indexOf("哈喽助手") + 4)
                        }

                        if (isStrValid(text)) {
                            aiuiHolder.sendMessage(text)
                            mAsr.stopListening()
                        }
                    }
                } else if (readyToDo) {
                    aiuiHolder.sendMessage(text)

                    readyToDo = false

                    mAsr.stopListening()
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
                if (autoListening && !speaking) {
                    mAsr.startListening(listener)
                }
            }

            override fun onError(speechError: SpeechError?) {
                Log.e("识别出错：${speechError?.errorCode}--->${speechError?.errorDescription}")

                if (autoListening && !speaking) {
                    mAsr.startListening(listener)
                }
            }
        }

        addChangeListener()
    }

    //调用aiui合成语句
    fun speakText(words: String) {
        speaking = true

        aiuiHolder.speakText(words)

        mAsr.stopListening()
    }

    fun startListening() {
        //防止重复启动
        if (autoListening) return

        autoListening = true

        mAsr.startListening(listener)
    }

    fun stopListening() {
        autoListening = false

        mAsr.cancel()
    }

    private fun addChangeListener() {
        aiuiHolder.speakOver
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    speaking = false

                    if (autoListening) {
                        mAsr.startListening(listener)
                    }
                }
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
                .doOnNext(result::onNext)
                .subscribe()

        aiuiHolder.music
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(music::onNext)
                .subscribe()
    }
}