package com.hello.widget.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import com.hello.R

class NoteListPopup(private val view: View) {
    private val popup: PopupWindow

    init {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        @SuppressLint("InflateParams")
        popup = PopupWindow(inflater.inflate(R.layout.popup_delete_item, null),
                WRAP_CONTENT, WRAP_CONTENT, true)
        popup.isTouchable = true
        popup.isOutsideTouchable = true
    }

    fun show() {
        popup.showAsDropDown(view)
    }
}