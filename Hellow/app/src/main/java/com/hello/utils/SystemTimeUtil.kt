package com.hello.utils

import android.os.SystemClock
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

object SystemTimeUtil {
    //获取系统开机时间
    @JvmStatic
    fun getPowerUpTime(): LocalDate {
        val upTime = (System.currentTimeMillis() - SystemClock.elapsedRealtime()).toInt()

        return LocalDateTime.now().minusSeconds(upTime / 1000).toLocalDate()
    }
}