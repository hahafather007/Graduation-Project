package com.hello.dagger.module;

import com.hello.view.fragment.TodayTodoFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class TodayTodoFragmentMoudle {
    @ContributesAndroidInjector
    abstract TodayTodoFragment fragment();
}
