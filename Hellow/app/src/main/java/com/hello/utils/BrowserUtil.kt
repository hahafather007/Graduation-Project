package com.hello.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hello.R
import com.hello.view.activity.WebViewActivity

object BrowserUtil {
    @JvmStatic
    fun openUrl(context: Context, url: String) {
        //先调用默认浏览器，若用户一个浏览器都没有，就调用WebViewActivity
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(WebViewActivity.intentOfUrl(context, url, context.getString(R.string.app_name)))
        }
    }
}