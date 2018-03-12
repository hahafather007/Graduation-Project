package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.hello.model.db.NotesHolder;
import com.hello.model.db.table.Note;
import com.hello.utils.rx.Completables;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

public class NoteViewModel {
    public ObservableList<Note> notes = new ObservableArrayList<>();

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

    public void addNote() {
        notesHolder.addNote("2333", "23333333")
                .compose(Completables.async())
                .subscribe();
    }

    private void getNotes() {
        notesHolder.getNotes()
                .compose(Singles.async())
                .subscribe(v -> notes.addAll(v));
    }
}
