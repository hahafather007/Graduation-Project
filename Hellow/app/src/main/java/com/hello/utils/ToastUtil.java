package com.hello.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by chengpengxiang on 2017/9/29.
 */

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
}
