package com.hello.utils;

public class Log {
    private final static String TAG_I = "Log.i----------------->";
    private final static String TAG_E = "Log.e----------------->";
    private final static String TAG_D = "Log.d----------------->";

    public static void i(String msg) {
        android.util.Log.i(TAG_I, msg);
    }

    public static void e(String msg) {
        android.util.Log.e(TAG_E, msg);
    }

    public static void d(String msg) {
        android.util.Log.d(TAG_D, msg);
    }
}
