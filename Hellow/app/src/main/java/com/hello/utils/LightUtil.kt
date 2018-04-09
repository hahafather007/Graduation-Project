@file:Suppress("DEPRECATION")

package com.hello.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build

object LightUtil {
    private var camera: Camera? = null

    @JvmStatic
    fun lightSwitch(context: Context, lightStatus: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return

        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        if (!lightStatus) { // 关闭手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", false)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                if (camera != null) {
                    camera?.stopPreview()
                    camera?.release()
                    camera = null
                }
            }
        } else { // 打开手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                val features = context.packageManager.systemAvailableFeatures
                for (f in features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH == f.name) { // 判断设备是否支持闪光灯
                        if (null == camera) {
                            camera = Camera.open()
                        }
                        val parameters = camera?.parameters
                        parameters?.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                        camera?.parameters = parameters
                        camera?.startPreview()
                    }
                }
            }
        }
    }
}