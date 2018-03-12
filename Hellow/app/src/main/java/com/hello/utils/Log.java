package com.hello.utils;

import android.annotation.SuppressLint;

import static com.hello.BuildConfig.DEBUG;

@SuppressLint("LogNotTimber")
public class Log {
    private final static String TAG_I = "Log.i----------------->";
    private final static String TAG_E = "Log.e----------------->";
    private final static String TAG_D = "Log.d----------------->";

    public static void i(Object msg) {
        if (DEBUG) {
            android.util.Log.i(TAG_I, msg != null ? msg.toString() : "null");
        }
    }

    public static void e(Exception e) {
        if (DEBUG) {
            e.printStackTrace();
        }
    }

    public static void e(Object msg) {
        if (DEBUG) {
            android.util.Log.e(TAG_I, msg != null ? msg.toString() : "null");
        }
    }

    public static void e(Throwable throwable) {
        if (DEBUG) {
            android.util.Log.e(TAG_E, throwable.getMessage());
        }
    }

    public static void d(Object msg) {
        if (DEBUG) {
            android.util.Log.d(TAG_D, msg != null ? msg.toString() : "null");
        }
    }
}
