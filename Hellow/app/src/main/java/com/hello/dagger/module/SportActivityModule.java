package com.hello.dagger.module;

import com.hello.view.activity.SportActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SportActivityModule {
    @ContributesAndroidInjector
    abstract SportActivity activity();
}
