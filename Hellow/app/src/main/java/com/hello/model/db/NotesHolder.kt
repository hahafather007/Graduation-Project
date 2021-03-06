package com.hello.model.db

import com.hello.model.db.table.Note
import com.hello.model.pref.HelloPref
import com.hello.utils.Log
import com.hello.utils.rx.Singles
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
    //用于缓存之前查找的结果
    private var cacheNotes = emptyList<Note>()

    //增删改后通知相关联界面进行数据刷新
    val statusChange: Subject<List<Note>> = PublishSubject.create()
    //note被编辑的回调
    val noteEdited: Subject<Note> = PublishSubject.create()
    //note增加的回调
    val noteAdded: Subject<Note> = PublishSubject.create()

    fun addNoteAuto(title: String, content: String, file: String): Completable {
        val time = LocalDateTime.now().toString()
        val note = Note(title, content, time, file, HelloPref.openId)

        return Completable.fromAction {
            note.save()
        }.doOnComplete {
            refreshNotes(RefreshType.ADDED, note)

            Log.i("Note表增加成功：Title=$title,Content=$content")
        }
    }

    fun addNote(title: String, content: String, time: String, file: String): Completable {
        val note = Note(title, content, time, file, HelloPref.openId)

        return Completable.fromAction {
            note.save()
        }
                .doOnComplete {
                    refreshNotes(RefreshType.ADDED, note)

                    Log.i("Note表增加成功：Title=$title,Content=$content")
                }
    }

    fun getNotes(): Single<List<Note>> {
        return if (cacheNotes.isEmpty())
            Single.just(Select().from(Note::class.java).queryList())
                    .map {
                        it.sortByDescending { it.time }
                        it.filter { it.openId == HelloPref.openId }
                    }
                    .doOnSuccess { cacheNotes = it }
        else
            Single.just(cacheNotes)
    }

    fun getNote(id: Long): Single<Note> {
        return Single.just(cacheNotes)
                .map { v -> v.forEach { if (it.id == id) return@map it } }
                .map { it as Note }
    }

    fun deleteNote(note: Note): Completable {
        return Completable.fromAction { note.delete() }
                .doOnComplete {
                    refreshNotes()
                    Log.i("Note表删除成功：Title=${note.title},Content=${note.content}")
                }

    }

    fun editNote(note: Note): Completable {
        note.time = LocalDateTime.now().toString()

        return Completable.fromAction { note.update() }
                .doOnComplete {
                    refreshNotes(RefreshType.EDITED, note)

                    Log.i("Note表编辑成功：Title=${note.title},Content=${note.content}")
                }
    }

    fun refreshNotes(type: RefreshType? = null, note: Note? = null) {
        Single.just(Select().from(Note::class.java).queryList())
                .map {
                    it.sortByDescending { it.time }
                    it.filter { it.openId == HelloPref.openId }
                }
                .compose(Singles.async())
                .subscribe { v ->
                    cacheNotes = v
                    statusChange.onNext(v)

                    when (type) {
                        RefreshType.ADDED -> noteAdded.onNext(note ?: Note())
                        RefreshType.EDITED -> noteEdited.onNext(note ?: Note())
                    }
                }
    }

    enum class RefreshType {
        ADDED,
        EDITED
    }
}