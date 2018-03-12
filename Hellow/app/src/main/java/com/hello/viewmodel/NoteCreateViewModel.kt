package com.hello.viewmodel

import android.databinding.ObservableField

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
    var noteText = ObservableField<String>()

    var error: Subject<Optional<*>> = PublishSubject.create()
    var deleteOver: Subject<Optional<*>> = PublishSubject.create()

    private lateinit var note: Note

    @Inject
    lateinit var voiceHolder: VoiceHolder
    @Inject
    lateinit var notesHolder: NotesHolder

    @Inject
    internal fun init() {
        voiceHolder.error
                .subscribe { error.onNext(Optional.empty<Any>()) }
    }

    fun initNote(id: Long) {
        //-1表示是新建一个note，不是查看以前的note
        if (id == -1L) return

        notesHolder.getNote(id)
                .compose(Singles.async())
                .subscribe { v ->
                    note = v
                    noteText.set(note.content)
                }
    }

    fun delete() {
        notesHolder.deleteNote(note)
                .compose(Completables.async())
                .subscribe { deleteOver.onNext(Optional.empty<Any>()) }
    }

    fun startRecord() {
        voiceHolder.startRecord()
    }

    fun stopRecord() {
        voiceHolder.stopRecord()
    }
}
