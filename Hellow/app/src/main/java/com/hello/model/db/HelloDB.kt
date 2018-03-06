package com.hello.model.db

import com.raizlabs.android.dbflow.annotation.Database

@Database(name = HelloDB.name, version = HelloDB.version)
class HelloDB {
    companion object {
        const val name = "helloDataBase"
        const val version = 1
    }
}