package com.hello.viewmodel

import android.databinding.ObservableField
import com.annimon.stream.Optional
import com.hello.common.RxController
import com.hello.model.aiui.AIUIHolder
import com.hello.model.db.NotesHolder
import com.hello.model.db.table.Note
import com.hello.utils.isStrValid
import com.hello.utils.rx.Completables
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteCreateViewModel @Inject constructor() : RxController() {
    val noteText = ObservableField<String>()

    val saveOver: Subject<Optional<*>> = PublishSubject.create()

    //用来防止计时器内容泄露
    private val timeDisposable = CompositeDisposable()
    private var cacheTitle = ""
    private var note: Note? = null
    //用来标识当前语音时间（每50秒一个循环）
    private var timeSeconds = 0

    @Inject
    lateinit var aiuiHolder: AIUIHolder
    @Inject
    lateinit var notesHolder: NotesHolder

    @Inject
    fun init() {
        aiuiHolder.noteText
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    if (isStrValid(noteText.get())) {
                        noteText.set(noteText.get() + "，" + it)
                    } else {
                        noteText.set(it)
                    }

                    //因为只能识别60秒，故手动循环
                    if (timeSeconds >= 50) {
                        stopRecord()
                        startRecord()

                        timeSeconds = 0
                    }
                }
                .subscribe()
    }

    fun initNote(id: Long) {
        //-1表示是新建一个note，不是查看以前的note
        if (id == -1L) return

        notesHolder.getNote(id)
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess {
                    note = it
                    noteText.set(note?.content)
                }
                .subscribe()
    }

    fun setNoteTitle(title: String) {
        note!!.title = title
    }

    fun getNoteTitle(): String = note?.title ?: cacheTitle

    fun saveNote() {
        note!!.content = noteText.get()

        notesHolder.editNote(note!!)
                .compose(Completables.async())
                .compose(Completables.disposable(compositeDisposable))
                .doOnComplete { saveOver.onNext(Optional.empty<Any>()) }
                .subscribe()
    }

    fun addNote(title: String, content: String) {
        notesHolder.addNote(title, content)
                .compose(Completables.async())
                .compose(Completables.disposable(compositeDisposable))
                .doOnComplete {
                    cacheTitle = title
                    saveOver.onNext(Optional.empty<Any>())
                }
                .subscribe()
    }

    fun startRecord() {
        aiuiHolder.startNoting()

        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(Observables.disposable(timeDisposable))
                .doOnNext { timeSeconds++ }
                .subscribe()
    }

    fun stopRecord() {
        aiuiHolder.stopRecording()

        timeDisposable.clear()
    }
}
