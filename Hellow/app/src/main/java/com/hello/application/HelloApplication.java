package com.hello.application;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.hello.dagger.component.DaggerApplicationComponent;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.raizlabs.android.dbflow.config.FlowManager;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;

import static com.hello.common.Constants.AIUI_APPID;


public class HelloApplication extends Application
        implements HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(this);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + AIUI_APPID);

        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());

        DaggerApplicationComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }
}
