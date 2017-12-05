package com.hello.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static Toast toast;

    public static void showToast(Context context, String s) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(Context context, int res) {
        String msg = context.getString(res);
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
