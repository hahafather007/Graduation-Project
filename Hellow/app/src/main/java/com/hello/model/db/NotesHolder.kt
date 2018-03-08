package com.hello.model.db

import com.annimon.stream.Optional
import com.hello.model.db.table.Note
import com.hello.utils.Log
import com.raizlabs.android.dbflow.sql.language.Select
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.joda.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

//语音笔记Note的管理仓库
@Singleton
class NotesHolder @Inject constructor() {
    //增删改后通知相关联界面进行数据刷新
    val statusChange: Subject<Optional<*>> = PublishSubject.create()

    fun addNote(title: String, content: String): Completable {
        return Completable.fromAction {
            val time = LocalDateTime.now().toString()
            val note = Note(title, content, time)
            note.save()
        }
                .doOnComplete {
                    statusChange.onNext(Optional.empty<Any>())
                    Log.i("Note表增加成功：Title=$title,Content=$content")
                }
    }

    fun getNotes(): Single<List<Note>> {
        return Single.just(Select().from(Note::class.java).queryList())
    }

    fun deleteNote(note: Note): Completable {
        return Completable.fromAction { note.delete() }
                .doOnComplete {
                    statusChange.onNext(Optional.empty<Any>())
                    Log.i("Note表删除成功：Title=${note.title},Content=${note.content}")
                }

    }

    fun editNote(note: Note): Completable {
        return Completable.fromAction { note.update() }
                .doOnComplete {
                    statusChange.onNext(Optional.empty<Any>())
                    Log.i("Note表编辑成功：Title=${note.title},Content=${note.content}")
                }

    }
}