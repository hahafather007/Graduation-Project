package com.hello.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract.Events.*
import com.hello.common.Constants.*
import org.joda.time.LocalDateTime
import java.util.*

object CalendarUtil {
    //    @JvmStatic
//    fun addCalendarEvent(context: Context, description: String, time: Long) {
//
//    }
//
//    //检查是否有现有存在的账户。存在则返回账户id，否则返回-1
//    private fun checkCalendarAccount(context: Context): Int {
//        val cursor = context.contentResolver.query(Constants.CALENDAR_URL,
//                null, null, null, null)
//        cursor.use { it ->
//            if (it == null) {
//                return -1
//            }
//            val count = it.count
//            return if (count > 0) {
//                it.moveToFirst()
//                it.getInt(it.getColumnIndex(CalendarContract.Calendars._ID))
//            } else {
//                -1
//            }
//        }
//    }
    @JvmStatic
    @SuppressLint("MissingPermission")
    fun addCalendarEvent(context: Context, data: LocalDateTime, description: String) {
        val time = Calendar.getInstance()
        time.set(data.year, data.monthOfYear - 1, data.dayOfMonth, data.hourOfDay, data.minuteOfHour)
        //日历事件
        val infoValues = ContentValues()
        //提醒事件，需要与日历事件配合
        val remindValue = ContentValues()
        val timeZone = TimeZone.getDefault()

        //插入的时间需要long型
        infoValues.put(DTSTART, time.timeInMillis)
        infoValues.put(DTEND, time.timeInMillis + 60000)
        infoValues.put(TITLE, "小哈提醒")
        infoValues.put(DESCRIPTION, description)
        //将系统时间毫秒数作为id
        infoValues.put(CALENDAR_ID, 3)
        infoValues.put(EVENT_LOCATION, "你所在的位置")
        infoValues.put(EVENT_TIMEZONE, timeZone.id)

        try {
            val uri = context.contentResolver.insert(CONTENT_URI, infoValues)
            // 得到当前表的_id
            val eventId = uri.lastPathSegment

            remindValue.put("event_id", eventId)
            remindValue.put("minutes", 5)
            remindValue.put("method", 1)
            context.contentResolver.insert(CALENDAR_REMIDER_URL, remindValue)
        } catch (e: Exception) {
            Log.e(e)
        }
    }
}