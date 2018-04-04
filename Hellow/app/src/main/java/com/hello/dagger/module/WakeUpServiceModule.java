package com.hello.dagger.module;

import com.hello.view.service.WakeUpService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class WakeUpServiceModule {
    @ContributesAndroidInjector
    abstract WakeUpService service();
}
