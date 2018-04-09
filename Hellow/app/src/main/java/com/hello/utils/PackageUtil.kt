package com.hello.utils

import android.content.Context
import android.content.pm.ApplicationInfo

object PackageUtil {
    //获取用户安装应用的包名
    @JvmStatic
    fun getUserPackages(context: Context): List<String> {
        val packages = context.packageManager.getInstalledPackages(0)

        return packages.filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                .map { it.packageName }
    }

    //通过包名运行程序
    @JvmStatic
    fun runAppByPackage(context: Context, packageName: String?) {
        if (packageName == null) return

        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //通过程序名运行
    @JvmStatic
    fun runAppByName(context: Context, name: String?) {
        if (name == null) return

        val manager = context.packageManager

        manager.getInstalledApplications(0)
                .forEach {
                    Log.i(it.loadLabel(manager))

                    if (it.loadLabel(manager) == name) {
                        runAppByPackage(context, it.packageName)
                        return
                    }
                }
    }

    @JvmStatic
    fun isAppInstalled(context: Context, packageName: String?): Boolean =
            packageName != null && getUserPackages(context).contains(packageName)
}