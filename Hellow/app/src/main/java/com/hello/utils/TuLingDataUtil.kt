package com.hello.utils

import android.content.Context
import com.hello.common.Constants.TULING_KEY
import com.hello.model.data.TuLingSendData
import com.hello.model.data.TuLingSendData.*
import com.hello.model.data.TuLingSendData.SendInfo.*

object TuLingDataUtil {
    @JvmStatic
    fun sendText(text: String, context: Context) =
            TuLingSendData(0, SendInfo(Text(text), null, null),
                    UserInfo(TULING_KEY, DeviceIdUtil.getId(context)))

}