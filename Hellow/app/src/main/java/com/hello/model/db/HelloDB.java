package com.hello.model.db;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = HelloDB.NAME, version = HelloDB.VERSION)
public class HelloDB {
    public static final String NAME = "helloDataBase";
    public static final int VERSION = 1;
}
