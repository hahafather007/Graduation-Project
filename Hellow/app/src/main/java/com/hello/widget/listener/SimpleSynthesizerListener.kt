package com.hello.widget.listener

import android.os.Bundle
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.SynthesizerListener


open class SimpleSynthesizerListener : SynthesizerListener{
    override fun onBufferProgress(p0: Int, p1: Int, p2: Int, p3: String?) {
    }

    override fun onSpeakBegin() {
    }

    override fun onSpeakProgress(p0: Int, p1: Int, p2: Int) {
    }

    override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
    }

    override fun onSpeakPaused() {
    }

    override fun onSpeakResumed() {
    }

    override fun onCompleted(errorMsg: SpeechError?) {
    }
}