package com.hello.utils

import android.content.Context

object VersionUtil {
    //获取版本号
    @JvmStatic
    fun getVersionName(context: Context): String =
            context.packageManager.getPackageInfo(context.packageName, 0).versionName

}