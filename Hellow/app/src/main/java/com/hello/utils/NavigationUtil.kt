package com.hello.utils

import android.content.Context
import android.content.Intent
import com.hello.R

object NavigationUtil {
    //调用该方法打开导航
    @Suppress("DEPRECATION")
    @JvmStatic
    fun openMap(context: Context, location: String) {
        try {
            val intent = Intent.getIntent("androidamap://route?sourceApplication=softname" +
                    "&sname=我的位置&dname=" + location + "&dev=0&m=0&t=1")

            context.startActivity(intent)
        } catch (e: Exception) {
            //如果出错就未安装高德地图，使用百度地图
            try {
                val intent = Intent.getIntent("intent://map/direction?origin=我的位置" +
                        "&destination=" + location + "&mode=driving" +
                        "&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;" +
                        "package=com.baidu.BaiduMap;end")

                context.startActivity(intent)
            } catch (e2: Exception) {
                ToastUtil.showToast(context, R.string.text_no_map)
            }

        }

    }
}