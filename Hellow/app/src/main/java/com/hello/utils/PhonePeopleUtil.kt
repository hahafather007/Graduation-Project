package com.hello.utils

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract.PhoneLookup


object PhonePeopleUtil {
    //根据电话号码获取联系人姓名
    @JvmStatic
    fun getDisplayNameByNumber(context: Context, number: String): String {
        var displayName: String = number
        var cursor: Cursor? = null

        try {
            val resolver = context.contentResolver
            val uri = PhoneLookup.CONTENT_FILTER_URI.buildUpon().appendPath(number).build()
            val projection = arrayOf("_id", "display_name")
            cursor = resolver.query(uri, projection, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                val columnIndexName = cursor.getColumnIndex("display_name")
                displayName = cursor.getString(columnIndexName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()

        }

        return displayName
    }
}