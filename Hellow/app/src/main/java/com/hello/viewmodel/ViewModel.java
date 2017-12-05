package com.hello.viewmodel;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ViewModel {
    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void onCleared() {
        compositeDisposable.dispose();
    }
}
