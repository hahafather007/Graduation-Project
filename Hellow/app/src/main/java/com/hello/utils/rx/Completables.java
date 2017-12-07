package com.hello.utils.rx;

import android.databinding.ObservableBoolean;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Completables {
    public static CompletableTransformer status(ObservableBoolean value) {
        return upstream -> upstream
                .doFinally(() -> value.set(false))
                .doOnSubscribe(__ -> value.set(true));
    }

    public static CompletableTransformer async() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static CompletableTransformer disposable(CompositeDisposable compositeDisposable) {
        return new CompletableTransformer() {
            Disposable disposable;

            @Override
            public CompletableSource apply(@NonNull Completable upstream) {
                return upstream
                        .doOnSubscribe(d -> {
                            disposable = d;
                            compositeDisposable.add(d);
                        })
                        .doFinally(() -> compositeDisposable.remove(disposable));
            }
        };
    }
}
