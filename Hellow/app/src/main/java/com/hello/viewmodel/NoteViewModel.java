package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.annimon.stream.Optional;
import com.hello.model.db.NotesHolder;
import com.hello.model.db.table.Note;
import com.hello.utils.rx.Completables;
import com.hello.utils.rx.Observables;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class NoteViewModel extends ViewModel {
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
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(v -> {
                    notes.clear();
                    notes.addAll(v);
                })
                .subscribe();

        getNotes();
    }

    public void deleteNote(Note note) {
        notesHolder.deleteNote(note)
                .compose(Completables.async())
                .compose(Completables.disposable(compositeDisposable))
                .doOnComplete(() -> deleteOver.onNext(Optional.empty()))
                .subscribe();
    }

    private void getNotes() {
        notesHolder.getNotes()
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(v -> notes.addAll(v))
                .subscribe();
    }
}
