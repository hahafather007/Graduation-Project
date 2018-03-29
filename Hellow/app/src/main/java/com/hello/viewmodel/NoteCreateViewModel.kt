package com.hello.viewmodel

import android.databinding.ObservableField
import android.databinding.ObservableInt

import com.annimon.stream.Optional
import com.hello.common.RxController
import com.hello.model.baidu.VoiceHolder
import com.hello.model.db.NotesHolder
import com.hello.model.db.table.Note
import com.hello.utils.rx.Completables
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles

import javax.inject.Inject

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class NoteCreateViewModel @Inject constructor() : RxController() {
    val noteText = ObservableField<String>()
    val decibe = ObservableInt()

    val error: Subject<Optional<*>> = PublishSubject.create()
    val saveOver: Subject<Optional<*>> = PublishSubject.create()

    private var holderText = ""
    private var cacheTitle = ""
    private var note: Note? = null

    @Inject
    lateinit var voiceHolder: VoiceHolder
    @Inject
    lateinit var notesHolder: NotesHolder

    @Inject
    fun init() {
        voiceHolder.error
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { error.onNext(Optional.empty<Any>()) }
                .subscribe()

        voiceHolder.part
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    noteText.set(noteText.get() + "，")
                    holderText = noteText.get() ?: ""
                }
                .subscribe()

        voiceHolder.result
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { noteText.set(holderText + it) }
                .subscribe()

        voiceHolder.decibel
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { decibe.set(it) }
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
        voiceHolder.startRecord()
    }

    fun stopRecord() {
        voiceHolder.stopRecord()
    }
}
