package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.annimon.stream.Optional;
import com.hello.model.db.NotesHolder;
import com.hello.model.db.table.Note;
import com.hello.utils.rx.Completables;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class NoteViewModel {
    public ObservableList<Note> notes = new ObservableArrayList<>();

    public Subject<Optional> deleteOver = PublishSubject.create();

    @Inject
    NotesHolder notesHolder;

    @Inject
    NoteViewModel() {

    }

    @Inject
    void init() {
        notesHolder.getStatusChange()
                .subscribe(v -> {
                    notes.clear();
                    notes.addAll(v);
                });

        getNotes();
    }

    public void deleteNote(Note note) {
        notesHolder.deleteNote(note)
                .compose(Completables.async())
                .subscribe(() -> deleteOver.onNext(Optional.empty()));
    }

    private void getNotes() {
        notesHolder.getNotes()
                .compose(Singles.async())
                .subscribe(v -> notes.addAll(v));
    }
}
