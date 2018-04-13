package com.hello.model.message

import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.hello.model.data.SystemMsgData
import io.reactivex.Single
import javax.inject.Inject

class MsgHolder @Inject constructor() {
    @Inject
    lateinit var context: Context

    //获取全部短信
    fun getMsgList() =
            Single.fromCallable {
                val msgs = mutableListOf<SystemMsgData>()
                var cursor: Cursor? = null

                try {
                    cursor = context.contentResolver.query(
                            Uri.parse("content://sms"),
                            arrayOf("_id", "address", "body", "date"),
                            null, null, "date desc")
                    while (cursor.moveToNext()) {
                        val msg = SystemMsgData(cursor.getString(cursor.getColumnIndex("address")),
                                cursor.getString(cursor.getColumnIndex("body")),
                                cursor.getString(cursor.getColumnIndex("date")))

                        msgs.add(msg)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }

                msgs
            }

    //获取最新的短信
}