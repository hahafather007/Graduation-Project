package com.hello.viewmodel

import android.databinding.ObservableField
import android.databinding.ObservableInt

import com.annimon.stream.Optional
import com.hello.model.baidu.VoiceHolder
import com.hello.model.db.NotesHolder
import com.hello.model.db.table.Note
import com.hello.utils.rx.Completables
import com.hello.utils.rx.Singles

import javax.inject.Inject

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class NoteCreateViewModel @Inject constructor() {
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
                .subscribe { error.onNext(Optional.empty<Any>()) }

        voiceHolder.part
                .subscribe {
                    noteText.set(noteText.get() + "，")
                    holderText = noteText.get()
                }

        voiceHolder.result
                .subscribe { noteText.set(holderText + it) }

        voiceHolder.decibel
                .subscribe { decibe.set(it) }
    }

    fun initNote(id: Long) {
        //-1表示是新建一个note，不是查看以前的note
        if (id == -1L) return

        notesHolder.getNote(id)
                .compose(Singles.async())
                .subscribe { v ->
                    note = v
                    noteText.set(note?.content)
                }
    }

    fun setNoteTitle(title: String) {
        note!!.title = title
    }

    fun getNoteTitle(): String = note?.title ?: cacheTitle

    fun saveNote() {
        note!!.content = noteText.get()

        notesHolder.editNote(note!!)
                .compose(Completables.async())
                .subscribe { saveOver.onNext(Optional.empty<Any>()) }
    }

    fun addNote(title: String, content: String) {
        notesHolder.addNote(title, content)
                .compose(Completables.async())
                .doOnComplete { cacheTitle = title }
                .subscribe { saveOver.onNext(Optional.empty<Any>()) }
    }

    fun startRecord() {
        voiceHolder.startRecord()
    }

    fun stopRecord() {
        voiceHolder.stopRecord()
    }
}
