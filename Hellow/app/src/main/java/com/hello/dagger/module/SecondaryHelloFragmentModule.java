package com.hello.dagger.module;

import com.hello.view.fragment.SecondaryHelloFragment;
import com.hello.view.fragment.SecondaryNewsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SecondaryHelloFragmentModule {
    @ContributesAndroidInjector
    abstract SecondaryHelloFragment helloFragment();
}
