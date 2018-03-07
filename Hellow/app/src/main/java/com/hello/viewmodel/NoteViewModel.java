package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.hello.model.db.table.Note;

import javax.inject.Inject;

public class NoteViewModel {
    public ObservableList<Note> notes = new ObservableArrayList<>();

    @Inject
    NoteViewModel() {
    }

    @Inject
    void init() {

    }
}
