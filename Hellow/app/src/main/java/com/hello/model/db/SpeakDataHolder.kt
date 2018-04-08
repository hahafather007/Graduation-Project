package com.hello.model.db

import com.hello.common.Constants
import com.hello.model.db.table.SpeakData
import com.hello.model.pref.HelloPref
import com.hello.utils.Log
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.Select
import io.reactivex.Completable
import io.reactivex.Single
import org.joda.time.LocalDateTime
import javax.inject.Inject

class SpeakDataHolder @Inject constructor() {
    fun addSpeakData(userTalk: String, helloTalk: String) =
            Completable.fromAction {
                SpeakData(HelloPref.openId, userTalk, helloTalk,
                        LocalDateTime.now().toString(Constants.DATA_TIME_FORMAT)).save()
            }
                    .doOnComplete { Log.i("SpeakData增加成功：$userTalk  $helloTalk") }

    fun getSpeakData() =
            Single.just(Select().from(SpeakData::class.java).queryList())
                    .doOnSuccess { Log.i("$it") }

    fun removeAll() =
            Completable.fromAction { Delete.tables(SpeakData::class.java) }
                    .doOnComplete { Log.i("SpeakData全部删除！！！") }
}