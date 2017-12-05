package com.hello.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chengpengxiang on 2017/9/29.
 */

public class IntentUtil {
    public static void setupActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }
}
