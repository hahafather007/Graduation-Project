package com.hello.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hello.model.pref.HelloPref
import com.hello.utils.NetWorkUtil
import com.hello.utils.ServiceUtil
import com.hello.view.service.WakeUpService

class WakeUpReceiver : BroadcastReceiver() {
    //接收到广播就立即启动service
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!ServiceUtil.isSerivceRunning(context!!, WakeUpService::class.java.name)
                && NetWorkUtil.isOnline(context) && HelloPref.isCanWakeup)
            context.startService(Intent(context, WakeUpService::class.java))
    }
}