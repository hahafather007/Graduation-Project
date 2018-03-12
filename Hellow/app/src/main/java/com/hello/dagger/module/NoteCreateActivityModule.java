package com.hello.dagger.module;

import com.hello.view.activity.NoteCreateActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class NoteCreateActivityModule {
    @ContributesAndroidInjector
    abstract NoteCreateActivity activity();
}
