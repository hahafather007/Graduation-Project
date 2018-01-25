package com.hello.utils

import android.content.Context
import android.provider.CalendarContract
import com.hello.common.Constants

object CalendarUtil {
    @JvmStatic
    fun addCalendarEvent(context: Context, description: String, time: Long) {

    }

    private fun calendarAccountRight(context: Context): Int {
        val cursor = context.contentResolver.query(Constants.CALANDER_URL, null, null, null, null)
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
}