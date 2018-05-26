package com.hello.utils

import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI
import android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER
import android.provider.ContactsContract.Contacts._ID
import android.provider.ContactsContract.PhoneLookup
import android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME
import android.provider.Telephony.Mms.Addr.CONTACT_ID
import android.support.v4.content.ContextCompat

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

    /**
     * 返回通讯录中记录，格式如下::
     *  姓名$$电话号码
     * @return
     */
    @SuppressLint("Recycle")
    @JvmStatic
    fun getContacts(context: Context): List<String> {
        val contacts = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(context, READ_CONTACTS) == PERMISSION_GRANTED) {
            val cr = context.contentResolver
            val cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val nameFieldColumnIndex = cursor.getColumnIndex(DISPLAY_NAME)
                    val personName = cursor.getString(nameFieldColumnIndex)
                    val contactId = cursor.getString(cursor.getColumnIndex(_ID))
                    val phone = cr.query(CONTENT_URI, null,
                            "$CONTACT_ID=$contactId", null, null)

                    //只取第一个联系电话
                    while (phone!!.moveToNext()) {
                        var phoneNumber = phone.getString(phone.getColumnIndex(NUMBER))
                        phoneNumber = phoneNumber.replace("-", "")
                        phoneNumber = phoneNumber.replace(" ", "")
                        contacts.add("$personName$$$phoneNumber")
                        break
                    }
                }

                cursor.close()
            }
        }

        return contacts
    }
}