package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.annimon.stream.Optional;
import com.hello.model.aiui.AIUIHolder;
import com.hello.model.data.TuLingData;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SecondaryHelloViewModel {
    public ObservableList<Object> items = new ObservableArrayList<>();

    public Subject<Optional<String>> urlOpen = PublishSubject.create();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    SecondaryHelloViewModel() {
    }

    @Inject
    void init() {
        aiuiHolder.aiuiResult
                .map(Optional::get)
                .subscribe(v -> {
                    items.add(v);
                    if (v instanceof TuLingData) {
                        urlOpen.onNext(Optional.of(((TuLingData) v).getUrl()));
                    }
                });
    }
}
