package com.hello.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.hello.R


object NotificationUtil {
    @JvmStatic
    fun getNotification(context: Context, showWhen: Boolean, autoCancel: Boolean,
                        title: Int, msg: Int, clickIntent: Intent?, channelId: Int?) =
            getNotification(context, showWhen, autoCancel, context.getString(title),
                    context.getString(msg), clickIntent, channelId)

    @Suppress("DEPRECATION")
    @JvmStatic
    fun getNotification(context: Context, showWhen: Boolean, autoCancel: Boolean,
                        title: String, msg: String, clickIntent: Intent?, channelId: Int?): Notification {
        val builder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification.Builder(context)
        } else {
            Notification.Builder(context, channelId.toString())
        }
                .setShowWhen(showWhen)
                .setAutoCancel(autoCancel)
                .setSmallIcon(R.drawable.img_logo_small)
                .setSound(Uri.EMPTY)
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

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.O)
    fun getChannel(id: Int): NotificationChannel {
        val channel = NotificationChannel(id.toString(),
                "helloChannel", NotificationManager.IMPORTANCE_DEFAULT)
        //是否在桌面icon右上角展示小红点
        channel.enableLights(false)
        channel.setSound(Uri.EMPTY, channel.audioAttributes)
        //是否在久按桌面图标时显示此渠道的通知
        channel.setShowBadge(false)
        channel.enableVibration(false);

        return channel
    }
}