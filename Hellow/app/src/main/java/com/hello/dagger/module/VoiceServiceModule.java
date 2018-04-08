package com.hello.dagger.module;

import com.hello.view.service.VoiceService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class VoiceServiceModule {
    @ContributesAndroidInjector
    abstract VoiceService service();
}
