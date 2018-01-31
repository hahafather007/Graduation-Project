package com.hello.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.provider.CalendarContract
import android.provider.CalendarContract.Events.*
import android.provider.CalendarContract.Reminders.EVENT_ID
import android.provider.CalendarContract.Reminders.METHOD
import android.provider.CalendarContract.Reminders.METHOD_ALERT
import android.provider.CalendarContract.Reminders.MINUTES
import com.hello.R
import com.hello.common.Constants.*
import java.util.*

object CalendarUtil {
    //检查是否有现有存在的账户。存在则返回账户id，否则返回-1
    private fun checkCalendarAccount(context: Context): Int {
        val cursor = context.contentResolver.query(CALENDAR_URL,
                null, null, null, null)
        cursor.use { it ->
            if (it == null) {
                return -1
            }
            val count = it.count
            return if (count > 0) {
                it.moveToFirst()
                it.getInt(it.getColumnIndex(CalendarContract.Calendars._ID))
            } else {
                -1
            }
        }
    }

    //账户创建成功则返回账户id，否则返回-1
    private fun addCalendarAccount(context: Context): Long {
        val timeZone = TimeZone.getDefault()
        val value = ContentValues()

        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME)
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER)
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.id)
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)

        var calendarUri = CALENDAR_URL
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build()

        val result = context.contentResolver.insert(calendarUri, value)
        return if (result == null) -1 else ContentUris.parseId(result)
    }

    //获取日历的ID
    private fun checkOrCreateCalendarAccount(context: Context): Int {
        val oldId = checkCalendarAccount(context)
        return if (oldId >= 0) {
            oldId
        } else {
            val addId = addCalendarAccount(context)
            if (addId >= 0) {
                checkCalendarAccount(context)
            } else {
                -1
            }
        }
    }

    //添加日程事件
    @JvmStatic
    @SuppressLint("MissingPermission")
    fun addCalendarEvent(context: Context, time: Calendar, description: String) {
        val calId = checkOrCreateCalendarAccount(context)

        //若日历id获取失败就返回
        if (calId < 0) return

        //日历事件
        val infoValues = ContentValues()
        //提醒事件，需要与日历事件配合
        val remindValue = ContentValues()
        val timeZone = TimeZone.getDefault()

        //插入的时间需要long型
        infoValues.put(DTSTART, time.timeInMillis)
        //事件持续时间1小时
        infoValues.put(DTEND, time.timeInMillis + 60000 * 60)
        infoValues.put(TITLE, description)
        //设置有闹钟提醒
        infoValues.put(CALENDAR_ID, calId)
        //设置时区为默认时区
        infoValues.put(EVENT_TIMEZONE, timeZone.id)

        try {
            val uri = context.contentResolver.insert(CONTENT_URI, infoValues)
            // 得到当前表的_id
            val eventId = uri.lastPathSegment

            remindValue.put(EVENT_ID, eventId)
            //提前5分钟提醒
            remindValue.put(MINUTES, 5)
            remindValue.put(METHOD, METHOD_ALERT)
            context.contentResolver.insert(CALENDAR_REMIDER_URL, remindValue)
        } catch (e: Exception) {
            ToastUtil.showToast(context, R.string.text_no_permission_calendar)
        }
    }
}