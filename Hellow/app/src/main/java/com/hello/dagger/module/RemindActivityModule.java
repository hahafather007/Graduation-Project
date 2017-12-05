package com.hello.dagger.module;

import com.hello.view.activity.RemindActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class RemindActivityModule {
    @ContributesAndroidInjector
    abstract RemindActivity activity();
}
