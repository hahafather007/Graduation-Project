package com.hello.widget;

import android.view.View;

import java.util.Calendar;

public abstract class SingleClickListener implements View.OnClickListener {
    private static final int MIN_CLICK_DELAY_TIME = 1000;

    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick();
        }
    }

    public abstract void onNoDoubleClick();
}
