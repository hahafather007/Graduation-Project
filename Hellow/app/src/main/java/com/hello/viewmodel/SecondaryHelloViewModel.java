package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.hello.common.RxController;
import com.hello.model.aiui.AIUIHolder;
import com.hello.model.data.TuLingData;
import com.hello.utils.Log;
import com.hello.utils.rx.Observables;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SecondaryHelloViewModel extends RxController {
    public ObservableList<Object> items = new ObservableArrayList<>();

    public Subject<TuLingData> tuLing = PublishSubject.create();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    SecondaryHelloViewModel() {
    }

    @Inject
    void init() {
        aiuiHolder.aiuiResult
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(v -> {
                    Log.i(v.toString());
                    items.add(v);
                    if (v instanceof TuLingData) {
                        tuLing.onNext(((TuLingData) v));
                    }
                })
                .subscribe();
    }
}
