package com.hello.application;

import android.app.Activity;
import android.app.Service;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.Fragment;

import com.chibatching.kotpref.Kotpref;
import com.hello.dagger.component.DaggerApplicationComponent;
import com.hello.utils.Log;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.raizlabs.android.dbflow.config.FlowManager;

import javax.inject.Inject;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;

import static com.hello.common.Constants.AIUI_APPID;


public class HelloApplication extends MultiDexApplication
        implements HasActivityInjector, HasSupportFragmentInjector, HasServiceInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(this);
        Kotpref.INSTANCE.init(this);

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + AIUI_APPID);

        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());

        DaggerApplicationComponent.builder()
                .application(this)
                .build()
                .inject(this);

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                Log.i("load成功！！！");
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }
}
