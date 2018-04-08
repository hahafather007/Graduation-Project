package com.hello.common;

import android.net.Uri;

public class Constants {
    //科大讯飞AIUI的appId
    public final static String AIUI_APPID = "59d6f1cb";
    //聚合数据新闻API的key
    public final static String NEWS_KEY = "c116bf742a3fa1f619a4632b1059c051";
    //下面两个为qq登录的app id和key
    public final static String QQ_APPID = "1106738863";
    public final static String QQ_KEY = "tFEWBKzD9alOR9yY";
    //这个聊天机器人会骂人
    public final static String BAD_TAKL_ROBUT = "http://api.qingyunke.com/";
    //图灵机器人key
    public final static String TULING_KEY = "b59d4513ab5d4e48adc362996732b9ac";
    public final static String TULING_KEY_BOY = "13d9c0231e064460809cc96876915dbd";
    //下面三个为日历的URL
    public final static Uri CALENDAR_URL = Uri.parse("content://com.android.calendar/calendars");
    public final static Uri CALENDAR_EVENT_URL = Uri.parse("content://com.android.calendar/events");
    public final static Uri CALENDAR_REMIDER_URL = Uri.parse("content://com.android.calendar/reminders");
    //日历的id
    public final static long CALENDAR_INSERT_ID = 3;
    //日历账户
    public final static String CALENDARS_NAME = "hello";
    public final static String CALENDARS_ACCOUNT_NAME = "hello@gmail.com";
    public final static String CALENDARS_ACCOUNT_TYPE = "com.hello";
    public final static String CALENDARS_DISPLAY_NAME = "哈喽助手";
    //网页URL参数的标识
    public final static String EXTRA_URL = "extra_url";
    public final static String EXTRA_TITLE = "extra_title";
    //Kotpref的名字
    public final static String KOTPREF_NAME = "hello_pref";
    //页面传值的标识
    public final static String EXTRA_ID = "extra_id";
    public final static String EXTRA_ITEM = "extra_item";

    public final static String DATA_FORMAT = "yyyy-MM-dd";
    public final static String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String ACTION_APP_CREATE = "action_app_create";
    public final static String ACTION_APP_DESTROY = "action_app_destroy";
}
