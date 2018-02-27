package com.hello.utils

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager

object DeviceIdUtil {
    @SuppressLint("HardwareIds")
    @Suppress("DEPRECATION")
    @JvmStatic
    fun getId(context: Context): String {
        return try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.deviceId
        } catch (e: SecurityException) {
            "1"
        }
    }
}