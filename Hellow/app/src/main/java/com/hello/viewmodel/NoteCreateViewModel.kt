package com.hello.viewmodel

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import com.annimon.stream.Optional
import com.hello.common.RxController
import com.hello.model.aiui.VoiceHolder
import com.hello.model.db.NotesHolder
import com.hello.model.db.table.Note
import com.hello.utils.isStrValid
import com.hello.utils.rx.Completables
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import javax.inject.Inject

class NoteCreateViewModel @Inject constructor() : RxController() {
    val noteText = ObservableField<String>()
    val volume = ObservableInt()
    val recording = ObservableBoolean()

    val saveOver: Subject<Optional<*>> = PublishSubject.create()

    private var cacheTitle = ""
    private var note: Note? = null

    @Inject
    lateinit var notesHolder: NotesHolder
    @Inject
    lateinit var voiceHolder: VoiceHolder

    @Inject
    fun init() {
        voiceHolder.resultText
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    val text = StringBuilder()

                    if (isStrValid(noteText.get())) {
                        text.append(noteText.get()).append(it).append("。")
                    } else {
                        text.append(it).append("。")
                    }

                    noteText.set(text.toString().replace(Regex("。，"), "，"))
                }
                .subscribe()

        voiceHolder.volume
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { volume.set(it) }
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
        voiceHolder.startRecording()

        recording.set(true)
    }

    fun stopRecord() {
        voiceHolder.stopRecording()

        recording.set(false)
    }

    override fun onCleared() {
        super.onCleared()

        voiceHolder.onCleared()
        recording.set(false)
    }
}
