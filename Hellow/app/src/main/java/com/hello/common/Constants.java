package com.hello.common;

import android.net.Uri;

public class Constants {
    //科大讯飞AIUI的appId
    public final static String AIUI_APPID = "59d6f1cb";
    //聚合数据新闻API的key
    public final static String NEWS_KEY = "c116bf742a3fa1f619a4632b1059c051";
    //下面三个为日历的URL
    public final static Uri CALENDAR_URL = Uri.parse("content://com.android.calendar/calendars");
    public final static Uri CALENDAR_EVENT_URL = Uri.parse("content://com.android.calendar/events");
    public final static Uri CALENDAR_REMIDER_URL = Uri.parse("content://com.android.calendar/reminders");
    //网页URL参数的标识
    public final static String URL = "url";
}
