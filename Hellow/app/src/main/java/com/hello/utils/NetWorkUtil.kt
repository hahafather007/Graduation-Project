package com.hello.utils

import android.content.Context
import android.net.ConnectivityManager

object NetWorkUtil {
    //检测网络是否可用
    @JvmStatic
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo

        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}