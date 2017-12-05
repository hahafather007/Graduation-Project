package com.hello.dagger;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.annimon.stream.function.Supplier;
import com.hello.view.fragment.ViewModelHolder;
import com.hello.viewmodel.ViewModel;

public interface ViewModelInjector<T extends ViewModel> {
    void inject(T instance);

    default T get(AppCompatActivity activity, Supplier<T> constructor) {
        return ViewModelHolder.get(activity, () -> init(constructor));
    }

    default T get(Fragment fragment, Supplier<T> constructor) {
        return ViewModelHolder.get(fragment, () -> init(constructor));
    }

    default T init(Supplier<T> constructor) {
        final T viewModel = constructor.get();
        inject(viewModel);
        return viewModel;
    }

    interface Builder<T extends ViewModel> {
        ViewModelInjector<T> build();
    }
}
