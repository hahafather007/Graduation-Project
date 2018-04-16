package com.hello.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.hello.utils.Log

open class PhoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //取来电号码（取到广播内容），   取广播值得键是系统定的，不能写别的
        val phony = intent.getStringExtra("incoming_number")
        if ("android.intent.action.PHONE_STATE" == intent.action) {
            //取到电话的服务   返回为，电话管理器
            val telecomsManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            //取到  来电的状态
            val state = telecomsManager.callState
            when (state) {
                //来电
                TelephonyManager.CALL_STATE_RINGING -> calling(phony)
                //接听
                TelephonyManager.CALL_STATE_OFFHOOK -> callingOn()
                //挂断
                TelephonyManager.CALL_STATE_IDLE -> callingOff()
            }

        }
    }

    //挂断
    open fun callingOff() {
        Log.i("挂断")
    }

    //响铃
    open fun calling(num: String) {
        Log.i("响铃:来电号码$num")
    }

    //通话
    open fun callingOn() {
        Log.i("接听")
    }
}