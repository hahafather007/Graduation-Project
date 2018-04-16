package com.hello.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import com.hello.R

object NotificationUtil {
    @JvmStatic
    fun getNotification(context: Context, showWhen: Boolean, autoCancel: Boolean,
                        title: Int, msg: Int, clickIntent: Intent?) =
            getNotification(context, showWhen, autoCancel, context.getString(title),
                    context.getString(msg), clickIntent)

    @Suppress("DEPRECATION")
    @JvmStatic
    fun getNotification(context: Context, showWhen: Boolean, autoCancel: Boolean,
                        title: String, msg: String, clickIntent: Intent?): Notification {
        val builder = Notification.Builder(context)
                .setShowWhen(showWhen)
                .setAutoCancel(autoCancel)
                .setSmallIcon(R.drawable.img_logo_small)
                .setLargeIcon((context.resources.getDrawable(R.mipmap.image_logo)
                        as BitmapDrawable).bitmap)
                .setContentTitle(title)
                .setContentText(msg)

        if (clickIntent != null) {
            val pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, 0)
            builder.setContentIntent(pendingIntent)
        }

        return builder.build()
    }
}