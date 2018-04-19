package com.hello.utils.rx;

import android.databinding.ObservableBoolean;

import com.annimon.stream.Optional;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

public class Observables {
    public static <T, R> ObservableTransformer<Optional<T>, Optional<R>> map(Function<T, R> mapper) {
        return upstream -> upstream
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(v -> Optional.ofNullable(mapper.apply(v)));
    }

    public static <T, R> ObservableTransformer<T, Optional<R>> mapNullable(Function<T, R> mapper) {
        return upstream -> upstream.map(v -> Optional.ofNullable(mapper.apply(v)));
    }

    public static <T extends Collection> ObservableTransformer<Optional<T>, T> skipIfEmpty() {
        return upstream -> upstream
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(v -> !v.isEmpty());
    }

    public static <T> ObservableTransformer<T, T> status(ObservableBoolean b) {
        return v -> v.doOnSubscribe(__ -> b.set(true))
                .doFinally(() -> b.set(false));
    }

    public static <T> ObservableTransformer<T, T> disposable(CompositeDisposable compositeDisposable) {
        return new ObservableTransformer<T, T>() {
            Disposable disposable;

            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .doOnSubscribe(d -> {
                            disposable = d;
                            compositeDisposable.add(d);
                        })
                        .doFinally(() -> compositeDisposable.remove(disposable));
            }
        };
    }

    public static <U, D> ObservableTransformer<U, D> map(BiFunction<U, U, D> diffFunction) {
        return s -> s.lift(o -> new MapObserver<>(o, diffFunction));
    }

    private static class MapObserver<U, D> implements Observer<U> {
        final Observer<? super D> actual;
        final BiFunction<U, U, D> diffFunction;

        Disposable disposable;
        U lastItem;

        MapObserver(Observer<? super D> actual, BiFunction<U, U, D> diffFunction) {
            this.actual = actual;
            this.diffFunction = diffFunction;
        }

        @Override
        public void onSubscribe(Disposable s) {
            if (DisposableHelper.validate(this.disposable, s)) this.disposable = s;
        }

        @Override
        public void onNext(U item) {
            if (disposable == DisposableHelper.DISPOSED) return;

            if (lastItem != null) {
                try {
                    actual.onNext(diffFunction.apply(lastItem, item));
                } catch (Throwable e) {
                    RxJavaPlugins.onError(e);
                }
            }
            lastItem = item;
        }

        @Override
        public void onError(Throwable e) {
            if (disposable != DisposableHelper.DISPOSED)
                actual.onError(e);
            else
                RxJavaPlugins.onError(e);
        }

        @Override
        public void onComplete() {
            if (disposable != DisposableHelper.DISPOSED) actual.onComplete();
        }
    }

    public static <T> ObservableTransformer<T, T> async() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
