package com.hello.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hello.view.service.WakeUpService

class WakeUpReceiver : BroadcastReceiver() {
    //接收到广播就立即启动service
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startService(Intent(context, WakeUpService::class.java))
    }
}