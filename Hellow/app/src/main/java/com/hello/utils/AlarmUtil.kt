package com.hello.utils

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import com.hello.R
import java.util.*

object AlarmUtil {
    //添加闹钟事件
    @JvmStatic
    fun addAlarmEvent(context: Context, time: Calendar, msg: String) {
        try {
            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM)
            alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, msg)
            alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, time.get(Calendar.HOUR_OF_DAY))
            alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, time.get(Calendar.MINUTE))
            alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            context.startActivity(alarmIntent)
        } catch (e: Exception) {
            ToastUtil.showToast(context, R.string.text_no_permission_alarm)
        }
    }
}