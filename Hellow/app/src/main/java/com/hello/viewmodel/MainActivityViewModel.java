package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

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

public class MainActivityViewModel extends RxController {
    public ObservableField<UpdateInfoData> updateInfo = new ObservableField<>();
    public ObservableBoolean updateLoading = new ObservableBoolean();
    public ObservableBoolean restoreLoading = new ObservableBoolean();
    public ObservableList<Note> notes = new ObservableArrayList<>();

    @Inject
    UpdateService updateService;
    @Inject
    BackupService backupService;
    @Inject
    NotesHolder notesHolder;

    @Inject
    MainActivityViewModel() {
    }

    @Inject
    void init() {
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
                            List<Long> ids = Stream.of(it).map(note -> note.id).toList();
                            Stream.of(v).forEach(note -> {
                                if (!ids.contains(note.id)) {
                                    noteList.add(note);
                                }
                            });

                            return noteList;
                        }))
                .compose(Singles.async())
                .compose(Singles.status(restoreLoading))
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(v -> {
                    notes.clear();
                    notes.addAll(v);

                    Log.i("notes====" + v);
                })
                .doOnError(Throwable::printStackTrace)
                .subscribe();
    }
}
