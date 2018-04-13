package com.hello.utils.databinding

import android.databinding.ObservableArrayList

fun <T> ObservableArrayList<T>.update(list: List<T>) {
    this.clear()
    this.addAll(list)
}