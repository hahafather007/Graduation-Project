package com.hello.model.aiui

import android.content.Context
import android.os.Bundle
import android.os.Environment
import cafe.adriel.androidaudioconverter.AndroidAudioConverter
import cafe.adriel.androidaudioconverter.callback.IConvertCallback
import cafe.adriel.androidaudioconverter.model.AudioFormat
import com.hello.common.RxController
import com.hello.model.pref.HelloPref
import com.hello.utils.*
import com.hello.utils.rx.Observables
import com.iflytek.cloud.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class VoiceHolder @Inject constructor() : RxController() {
    //用于记录当前说活时长
    private var speakTime = 0
    //标记当前是否真正说话
    private var speaking = false
    //调用startRecording的次数
    private var times = 0
    //转换后的文件名
    private var fileName = ""

    //每句话识别的结果
    val resultText: Subject<String> = PublishSubject.create()
    //音量大小
    val volume: Subject<Int> = PublishSubject.create()
    val loading: Subject<Boolean> = PublishSubject.create()

    private lateinit var mAsr: SpeechRecognizer
    //识别监听器
    private lateinit var listener: RecognizerListener

    @Inject
    lateinit var context: Context

    @Inject
    fun init() {
        mAsr = SpeechRecognizer.createRecognizer(context, null)
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat")
        mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        mAsr.setParameter(SpeechConstant.ACCENT, "mandarin")
        mAsr.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")

        listener = object : RecognizerListener {
            //data表示音频数据
            //音量值0~30
            override fun onVolumeChanged(vol: Int, data: ByteArray?) {
                volume.onNext(vol)

                Log.i("音量：$vol")
            }

            override fun onResult(result: RecognizerResult?, isLast: Boolean) {
                val text = SpeechJsonParser.parseGrammarResult(result?.resultString)

                if (isStrValid(text)) {
                    resultText.onNext(text)
                }

                if (speaking && speakTime >= 40) {
                    mAsr.stopListening()

                    val cacheFile = File("${Environment.getExternalStorageDirectory()}" +
                            "/哈喽助手/录音/缓存/${times - 1}.wav")

                    Observable.interval(16, TimeUnit.MILLISECONDS)
                            .filter { cacheFile.exists() }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(Observables.disposable(compositeDisposable))
                            .doOnNext {
                                speakTime = 0

                                if (speaking) {
                                    startRecording()
                                }

                                compositeDisposable.clear()
                            }
                            .subscribe()
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
                    mAsr.stopListening()

                    val cacheFile = File("${Environment.getExternalStorageDirectory()}" +
                            "/哈喽助手/录音/缓存/${times - 1}.wav")

                    Observable.interval(16, TimeUnit.MILLISECONDS)
                            .filter { cacheFile.exists() }
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .compose(Observables.disposable(compositeDisposable))
                            .doOnNext {
                                speakTime = 0

                                if (speaking) {
                                    startRecording()
                                }

                                compositeDisposable.clear()
                            }
                            .subscribe()
                }
            }

            override fun onError(error: SpeechError?) {
                Log.e("识别出错：${error?.errorCode}--->${error?.errorDescription}")
            }
        }
    }

    fun startRecording() {
        mAsr.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory().toString() + "/哈喽助手/录音/缓存/${times++}.wav")

        speaking = true

        mAsr.startListening(listener)

        Observable.interval(40, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { speakTime++ }
                .subscribe()

    }

    //停止识别，有返回结果
    fun stopRecording() {
        speaking = false

        mAsr.stopListening()

        loading.onNext(true)

        Observable.timer(1, TimeUnit.SECONDS)
                .flatMap { Observable.just(decodeFile()) }
                .compose(Observables.async())
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    times = 0

                    wavToMp3()
                }
                .doOnError {
                    Log.e(it)
                    fileName = ""
                }
                .subscribe()
    }

    fun getFileName() = fileName

    //将录音数据合并
    private fun decodeFile() {
        val files = (0 until times)
                .map {
                    File("${Environment.getExternalStorageDirectory()}" +
                            "/哈喽助手/录音/缓存/$it.wav")
                }

        fileName = "${Environment.getExternalStorageDirectory()}" +
                "/哈喽助手/录音/${HelloPref.name}${System.currentTimeMillis()}.wav"

        //合并文件的名字为设备号+系统当前时间
        WavMergeUtil.mergeWav(files, File(fileName))

        //删除缓存文件
        FileDeleteUtil.deleteDirectory("${Environment.getExternalStorageDirectory()}" +
                "/哈喽助手/录音/缓存/")
    }

    private fun wavToMp3() {
        val callback = object : IConvertCallback {
            override fun onSuccess(convertedFile: File?) {
                //删除缓存文件
                FileDeleteUtil.deleteFile(fileName)
                fileName = convertedFile?.absolutePath ?: ""

                Log.i("转换成功：$fileName")

                loading.onNext(false)
            }

            override fun onFailure(e: Exception?) {
                e?.printStackTrace()

                loading.onNext(false)
            }
        }
        AndroidAudioConverter.with(context)
                .setFile(File(fileName))
                .setFormat(AudioFormat.MP3)
                .setCallback(callback)
                .convert()
    }

    //取消识别，无返回结果
    private fun cancelRecording() {
        mAsr.cancel()

        speaking = false
    }

    override fun onCleared() {
        super.onCleared()

        cancelRecording()
        mAsr.destroy()
    }
}