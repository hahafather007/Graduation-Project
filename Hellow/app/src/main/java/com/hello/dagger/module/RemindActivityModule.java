package com.hello.dagger.module;

import com.hello.view.activity.HelpActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class RemindActivityModule {
    @ContributesAndroidInjector
    abstract HelpActivity activity();
}
