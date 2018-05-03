package com.hello.dagger.module;

import com.hello.view.activity.NoteRestoreActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class NoteRestoreActivityModule {
    @ContributesAndroidInjector
    abstract NoteRestoreActivity activity();
}
