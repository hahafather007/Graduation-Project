package com.hello.dagger.module;

import android.content.Context;

import com.hello.application.HelloApplication;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ApplicationModule {
    @Binds
    abstract Context provideApplicationContext(HelloApplication application);
}
