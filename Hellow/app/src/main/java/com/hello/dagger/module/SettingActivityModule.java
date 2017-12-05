package com.hello.dagger.module;

import com.hello.view.activity.SettingActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SettingActivityModule {
    @ContributesAndroidInjector
    abstract SettingActivity activity();
}
