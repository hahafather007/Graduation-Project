package com.hello.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.annimon.stream.function.Supplier;
import com.hello.common.RxController;

public class ViewModelHolder<T extends RxController> extends Fragment {
    private static final String TAG = ViewModelHolder.class.toString();

    private T viewModel;

    public static <T extends RxController> T get(AppCompatActivity activity, Supplier<T> constructor) {
        return get(activity.getSupportFragmentManager(), constructor);
    }

    public static <T extends RxController> T get(Fragment fragment, Supplier<T> constructor) {
        return get(fragment.getChildFragmentManager(), constructor);
    }

    private static <T extends RxController> T get(FragmentManager fm, Supplier<T> constructor) {
        //noinspection unchecked
        ViewModelHolder<T> holder = (ViewModelHolder<T>) fm.findFragmentByTag(TAG);
        if (holder == null) {
            holder = new ViewModelHolder<>();
            fm.beginTransaction()
                    .add(holder, TAG)
                    .commit();
        }

        if (holder.viewModel == null)
            holder.viewModel = constructor.get();

        return holder.viewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        viewModel.onCleared();
    }
}
