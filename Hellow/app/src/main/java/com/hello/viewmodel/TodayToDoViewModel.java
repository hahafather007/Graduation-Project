package com.hello.viewmodel;

import android.databinding.ObservableInt;

import com.annimon.stream.Optional;
import com.hello.common.RxController;
import com.hello.model.aiui.AIUIHolder;
import com.hello.utils.rx.Observables;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class TodayToDoViewModel extends RxController {
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
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(__ -> error.onNext(Optional.empty()))
                .subscribe();

        aiuiHolder.volume
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(volume::set)
                .subscribe();
    }

    public void startRecording(){
        aiuiHolder.startRecording();
    }

    public void stopRecording(){
        aiuiHolder.stopRecording();
    }

    public void sendMessage(String msg) {
        aiuiHolder.sendMessage(msg);
    }
}
