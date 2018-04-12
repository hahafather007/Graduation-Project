package com.hello.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build


object AppRunningUtil {
    //5.0之前判断前台应用
    @Suppress("DEPRECATION")
    private fun getTopAppPackageNameBeforeLolliPop(context: Context): String {
        var packageName = ""
        try {
            val mActivityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val rti = mActivityManager.getRunningTasks(1)
            packageName = rti[0].topActivity.packageName
        } catch (ignored: Exception) {
        }

        return packageName
    }

    //5.0之后判断前台应用
    private fun getTopAppPackageNameAfterLolliPop(context: Context): String {
        val packageName = ""
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            val processes = activityManager.runningAppProcesses
            if (processes.size == 0) {
                return packageName
            }
            for (process in processes) {

                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return process.processName
                }
            }
        } catch (ignored: Exception) {
        }

        return packageName
    }

    //获取当前前台程序的包名
    @JvmStatic
    fun getTopAppPackageName(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val name = getTopAppPackageNameBeforeLolliPop(context)
            Log.i("前台app：$name")

            name

        } else {
            val name = getTopAppPackageNameAfterLolliPop(context)
            Log.i("前台app：$name")

            name
        }
    }

    //用来判断当前app是不是前台应用
    @JvmStatic
    fun isThisAppTop(context: Context) = isAppTop(context, context.packageName)

    //通过包名判断指定app是不是前台应用
    @JvmStatic
    fun isAppTop(context: Context, packageName: String) = getTopAppPackageName(context) == packageName
}