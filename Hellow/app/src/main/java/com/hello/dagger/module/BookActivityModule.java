package com.hello.dagger.module;

import com.hello.view.activity.BookActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BookActivityModule {
    @ContributesAndroidInjector
    abstract BookActivity activity();
}
