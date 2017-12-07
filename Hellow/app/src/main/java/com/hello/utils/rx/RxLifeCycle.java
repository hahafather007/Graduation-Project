package com.hello.utils.rx;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;

public class RxLifeCycle {
    /**
     * Use {@link #resumed(AppCompatActivity)} instead.
     */
    @Deprecated
    public static <T> ObservableTransformer<T, T> with(AppCompatActivity activity) {
        return resumed(activity);
    }

    public static <T> ObservableTransformer<T, T> resumed(AppCompatActivity activity) {
        return s -> s.lift(v -> new ActivityLifeCycleObserver<T>(v, activity) {
            public void onActivityResumed(Activity activity) {
                onActive(activity);
            }

            public void onActivityPaused(Activity activity) {
                onInactive(activity);
            }
        });
    }

    public static <T> ObservableTransformer<T, T> started(AppCompatActivity activity) {
        return s -> s.lift(v -> new ActivityLifeCycleObserver<T>(v, activity) {
            @Override
            public void onActivityStarted(Activity activity) {
                onActive(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                onInactive(activity);
            }
        });
    }

    public static <T> ObservableTransformer<T, T> created(AppCompatActivity activity) {
        return s -> s.lift(v -> new ActivityLifeCycleObserver<T>(v, activity) {
            @Override
            public void onSubscribe(Disposable s) {
                super.onSubscribe(s);

                if (!activity.isFinishing()) onActive(activity);
            }
        });
    }

    private static abstract class ActivityLifeCycleObserver<T>
            extends SimpleActivityLifecycleCallbacks implements Observer<T>, Disposable {

        final Observer<? super T> actual;
        final AppCompatActivity activity;

        Application application;

        boolean active;
        T item;

        Disposable s;

        ActivityLifeCycleObserver(Observer<? super T> actual, AppCompatActivity activity) {
            this.actual = actual;
            this.activity = activity;
        }

        @Override
        public void onSubscribe(Disposable s) {
            if (activity.isFinishing()) {
                s.dispose();
                return;
            }

            if (DisposableHelper.validate(this.s, s)) {
                this.s = s;
                actual.onSubscribe(this);

                application = activity.getApplication();
                application.registerActivityLifecycleCallbacks(this);
            }
        }

        void onActive(Activity a) {
            if (a != activity) return;

            active = true;
            if (item != null) {
                if (!isDisposed()) {
                    try {
                        actual.onNext(item);
                    } catch (Throwable e) {
                        RxJavaPlugins.onError(e);
                    }
                }
                item = null;
            }
        }

        void onInactive(Activity a) {
            if (a == activity) active = false;
        }

        @Override
        public void onActivityDestroyed(Activity a) {
            if (a == activity) dispose();
        }

        @Override
        public void onNext(T t) {
            if (active)
                actual.onNext(t);
            else
                item = t;
        }

        @Override
        public void onError(Throwable t) {
            if (s != DisposableHelper.DISPOSED) {
                actual.onError(t);
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        @Override
        public void onComplete() {
            if (s != DisposableHelper.DISPOSED) {
                actual.onComplete();
            }
        }

        @Override
        public void dispose() {
            s.dispose();

            application.unregisterActivityLifecycleCallbacks(this);
        }

        @Override
        public boolean isDisposed() {
            return s.isDisposed();
        }
    }

    public static <T> ObservableTransformer<T, T> with(Fragment fragment) {
        return new ObservableTransformer<T, T>() {
            FragmentManager fm;
            FragmentManager.FragmentLifecycleCallbacks callbacks;

            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream
                        .doOnSubscribe(disposable -> {
                            if (fragment.isDetached()) {
                                disposable.dispose();
                                return;
                            }

                            callbacks = new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentDetached(FragmentManager fm, Fragment f) {
                                    if (f == fragment) disposable.dispose();
                                }
                            };
                            fm = fragment.getFragmentManager();
                            fm.registerFragmentLifecycleCallbacks(callbacks, false);
                        })
                        .doFinally(() -> fm.unregisterFragmentLifecycleCallbacks(callbacks));
            }
        };
    }
}
