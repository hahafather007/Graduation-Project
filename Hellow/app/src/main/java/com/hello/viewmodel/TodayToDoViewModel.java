package com.hello.viewmodel;

import android.databinding.ObservableInt;

import com.annimon.stream.Optional;
import com.hello.model.aiui.AIUIHolder;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class TodayToDoViewModel {
    public ObservableInt volume = new ObservableInt();
    public Subject<Optional> error = PublishSubject.create();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    TodayToDoViewModel() {
    }

    @Inject
    void init() {
        aiuiHolder.error
                .subscribe(__ -> error.onNext(Optional.empty()));

        aiuiHolder.volume
                .subscribe(volume::set);
    }

    public void startOrStopRecording() {
        aiuiHolder.startOrStopRecording();
    }

    public void sendMessage(String msg) {
        aiuiHolder.sendMessage(msg);
    }
}
