package com.hello.common;

import io.reactivex.disposables.CompositeDisposable;

public abstract class RxController {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void onCleared() {
        compositeDisposable.dispose();
    }
}
