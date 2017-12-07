package com.hello.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class VpSwipeRefreshLayout extends SwipeRefreshLayout {

    private float startY;
    private float startX;
    private boolean mIsVpDragger;
    private final int mTouchSlop;

    public VpSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 根据手指滑动的x和y方向的差值
     * 判断用户是否在滑动其它支持左右滑动的和控件
     * 防止滑动冲突
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                startX = ev.getX();
                mIsVpDragger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsVpDragger) {
                    return false;
                }
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsVpDragger = true;
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsVpDragger = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}