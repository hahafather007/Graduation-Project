package com.hello.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class NoTouchScrollView extends ScrollView {
    public NoTouchScrollView(Context context) {
        super(context);
    }

    public NoTouchScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
