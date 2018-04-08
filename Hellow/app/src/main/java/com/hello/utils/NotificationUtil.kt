package com.hello.utils

import android.app.Notification
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import com.hello.R

object NotificationUtil {
    @Suppress("DEPRECATION")
    @JvmStatic
    fun getNotification(context: Context, showWhen: Boolean, autoCancel: Boolean,
                        title: Int, msg: Int) =
            Notification.Builder(context)
                    .setShowWhen(showWhen)
                    .setAutoCancel(autoCancel)
                    .setSmallIcon(R.drawable.img_logo_small)
                    .setLargeIcon((context.resources.getDrawable(R.mipmap.image_logo)
                            as BitmapDrawable).bitmap)
                    .setContentTitle(context.getString(title))
                    .setContentText(context.getString(msg))
                    .build()!!
}