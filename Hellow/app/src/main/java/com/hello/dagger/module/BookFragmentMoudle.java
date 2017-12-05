package com.hello.dagger.module;

import com.hello.view.fragment.BookFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BookFragmentMoudle {
    @ContributesAndroidInjector
    abstract BookFragment fragment();
}
