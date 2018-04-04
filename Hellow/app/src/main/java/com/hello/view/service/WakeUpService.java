package com.hello.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hello.utils.Log;
import com.hello.viewmodel.WakeUpViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class WakeUpService extends Service {
    private WakeUpBinder binder;

    @Inject
    WakeUpViewModel viewModel;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind：WakeUpService");

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("onUnbind：WakeUpService");

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("onCreate：WakeUpService");

        AndroidInjection.inject(this);

        binder = new WakeUpBinder();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy：WakeUpService");

        super.onDestroy();
    }

    //返回Binder，可以供activity调用
    public class WakeUpBinder extends Binder {
        public WakeUpService getService() {
            return WakeUpService.this;
        }
    }
}
