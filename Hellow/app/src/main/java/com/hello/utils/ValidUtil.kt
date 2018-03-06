package com.hello.utils

fun isStrValid(s: String?) = s != null && !s.isEmpty()

fun isObjValid(any: Any?) = any != null

fun <T> isListValid(list: List<T>?) = list != null && list.isNotEmpty()