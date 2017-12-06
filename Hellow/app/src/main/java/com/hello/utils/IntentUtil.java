package com.hello.utils;

import android.content.Context;
import android.content.Intent;

public class IntentUtil {
    public static void setupActivity(Context context, Class activity) {
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }
}
