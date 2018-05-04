package com.hello.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.annimon.stream.Stream;
import com.hello.common.RxController;
import com.hello.model.data.UpdateInfoData;
import com.hello.model.db.NotesHolder;
import com.hello.model.db.table.Note;
import com.hello.model.service.BackupService;
import com.hello.model.service.UpdateService;
import com.hello.utils.Log;
import com.hello.utils.rx.Singles;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainActivityViewModel extends RxController {
    public ObservableField<UpdateInfoData> updateInfo = new ObservableField<>();
    public ObservableBoolean updateLoading = new ObservableBoolean();
    public ObservableBoolean restoreLoading = new ObservableBoolean();

    public Subject<List<Note>> notes = PublishSubject.create();

    @Inject
    UpdateService updateService;
    @Inject
    BackupService backupService;
    @Inject
    NotesHolder notesHolder;

    @Inject
    MainActivityViewModel() {
    }

    public void checkUpdate() {
        if (updateLoading.get()) return;

        updateService.getVersion()
                .compose(Singles.async())
                .compose(Singles.status(updateLoading))
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(updateInfo::set)
                .subscribe();
    }

    public void restoreNote() {
        if (restoreLoading.get()) return;

        backupService.restoreNote()
                .flatMap(v -> notesHolder.getNotes()
                        .map(it -> {
                            List<Note> noteList = new ArrayList<>();
                            List<String> times = Stream.of(it).map(note -> note.time).toList();
                            Stream.of(v).forEach(note -> {
                                if (!times.contains(note.time)) {
                                    noteList.add(note);
                                }
                            });

                            return noteList;
                        }))
                .compose(Singles.async())
                .compose(Singles.status(restoreLoading))
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(v -> {
                    notes.onNext(v);

                    Log.i("notes====" + v);
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }

    public void refreshNotes() {
        notesHolder.refreshNotes(null, null);
    }
}
