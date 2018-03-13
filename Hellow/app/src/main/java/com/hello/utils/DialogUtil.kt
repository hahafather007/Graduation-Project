package com.hello.utils

import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.support.v7.app.AlertDialog
import com.hello.R

object DialogUtil {
    private var dialog: AlertDialog? = null

    //显示两个按钮都有的dialog
    //cancelText表示"取消"按键的文字，可为空
    //enterText同理
    @JvmStatic
    fun showDialog(context: Context, msg: Int, cancelText: Int?, enterText: Int?,
                   cancelListener: OnClickListener?, enterListener: OnClickListener?) {
        dialog?.dismiss()
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.title_dialog)
                .setMessage(msg)

        if (cancelText != null) {
            builder.setNegativeButton(cancelText, cancelListener)
        }
        if (enterText != null) {
            builder.setPositiveButton(enterText, enterListener)
        }

        dialog = builder.create()

        dialog?.show()
    }
}