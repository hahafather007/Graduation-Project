package com.hello.utils.rx;

import android.databinding.ObservableBoolean;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Singles {
    public static <T> SingleTransformer<T, T> status(ObservableBoolean value) {
        return upstream -> upstream
                .doFinally(() -> value.set(false))
                .doOnSubscribe(__ -> value.set(true));
    }

    public static <T> SingleTransformer<T, T> async() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> SingleTransformer<T, T> disposable(CompositeDisposable compositeDisposable) {
        return new SingleTransformer<T, T>() {
            Disposable disposable;

            @Override
            public SingleSource<T> apply(@NonNull Single<T> upstream) {
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
