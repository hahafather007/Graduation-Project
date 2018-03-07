package com.hello.dagger.module;

import com.hello.view.fragment.NoteFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class NoteFragmentMoudle {
    @ContributesAndroidInjector
    abstract NoteFragment fragment();
}
