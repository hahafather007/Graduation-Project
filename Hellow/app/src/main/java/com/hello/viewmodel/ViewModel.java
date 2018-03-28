package com.hello.viewmodel;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ViewModel {
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void onCleared() {
        compositeDisposable.dispose();
    }
}
