package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.annimon.stream.Optional;
import com.hello.model.aiui.AIUIHolder;

import javax.inject.Inject;

public class SecondaryHelloViewModel {
    public ObservableList<Object> items = new ObservableArrayList<>();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    SecondaryHelloViewModel() {
    }

    @Inject
    void init() {
        aiuiHolder.aiuiResult
                .map(Optional::get)
                .subscribe(items::add);
    }
}
