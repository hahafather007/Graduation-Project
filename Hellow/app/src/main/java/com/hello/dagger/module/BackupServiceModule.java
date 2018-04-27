package com.hello.dagger.module;

import com.hello.view.service.BackupService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class BackupServiceModule {
    @ContributesAndroidInjector
    abstract BackupService service();
}
