package com.hello.viewmodel;

import com.annimon.stream.Optional;
import com.hello.model.aiui.AIUIHolder;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class TodayToDoViewModel {
    public Subject<Optional> error = PublishSubject.create();
    public Subject<Integer> volume = PublishSubject.create();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    TodayToDoViewModel() {
    }

    @Inject
    void init() {
        aiuiHolder.error
                .subscribe(__ -> error.onNext(Optional.empty()));
    }

    public void startOrStopRecording() {
        aiuiHolder.startOrStopRecording();
    }

    public void sendMessage(String msg) {
        aiuiHolder.sendMessage(msg);
    }
}
