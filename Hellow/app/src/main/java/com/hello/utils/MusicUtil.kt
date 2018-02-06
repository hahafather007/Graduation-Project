package com.hello.utils

import android.media.AudioManager
import android.media.MediaPlayer
import com.hello.utils.rx.Observables
import io.reactivex.Observable

object MusicUtil {
    private val player = MediaPlayer()

    @JvmStatic
    fun playMusic(url: String) {
        playMusic(url, null)
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun playMusic(url: String, listener: MediaErrorListener?) {
        if (player.isPlaying) {
            player.stop()
        }
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)

        //异步进行音乐播放，以免阻塞线程
        Observable.just(url)
                .map { v ->
                    player.reset()
                    player.setDataSource(v)
                    player.setOnErrorListener({ _, _, _ ->
                        listener?.error()
                        false
                    })
                    player.prepare()
                    player
                }
                .compose(Observables.async())
                .subscribe(MediaPlayer::start)
    }

    @JvmStatic
    fun stopMusic() {
        if (player.isPlaying) {
            player.stop()
        }
    }

    interface MediaErrorListener {
        fun error()
    }
}