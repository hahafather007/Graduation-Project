package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.annimon.stream.Optional;
import com.hello.model.baidu.VoiceHolder;
import com.hello.model.db.NotesHolder;
import com.hello.model.db.table.Note;
import com.hello.utils.rx.Completables;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class NoteViewModel {
    public ObservableList<Note> notes = new ObservableArrayList<>();

    public Subject<Optional> error = PublishSubject.create();

    @Inject
    NotesHolder notesHolder;
    @Inject
    VoiceHolder voiceHolder;

    @Inject
    NoteViewModel() {

    }

    @Inject
    void init() {
        voiceHolder.getError()
                .subscribe(__ -> error.onNext(Optional.empty()));

        notesHolder.getStatusChange()
                .subscribe(__ -> getNotes());

        getNotes();
    }

    public void addNote() {
        notesHolder.addNote("2333", "23333333")
                .compose(Completables.async())
                .subscribe();
    }

    public void startRecord() {
        voiceHolder.startRecord();
    }

    public void stopRecord() {
        voiceHolder.stopRecord();
    }

    private void getNotes() {
        notesHolder.getNotes()
                .compose(Singles.async())
                .subscribe(v -> notes.addAll(v));
    }
}
