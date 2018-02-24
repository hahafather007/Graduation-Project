package com.hello.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager

object MacIdUtil {
    @SuppressLint("HardwareIds")
    @JvmStatic
    fun getId(context: Context): String {
        val wm = (context.applicationContext.getSystemService(WIFI_SERVICE)) as WifiManager
        return wm.connectionInfo.macAddress
    }
}