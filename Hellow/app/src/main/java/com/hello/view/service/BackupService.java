package com.hello.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hello.utils.Log;
import com.hello.viewmodel.BackupViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class BackupService extends Service {
    private BackupBinder binder;

    @Inject
    BackupViewModel viewModel;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind：BackupService");

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("onUnbind：BackupService");

        stopForeground(true);

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("onCreate：BackupService");

        AndroidInjection.inject(this);

        binder = new BackupBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy：BackupService");

        viewModel.onCleared();

        super.onDestroy();
    }

    //返回Binder，可以供activity调用
    public class BackupBinder extends Binder {
        public BackupService getService() {
            return BackupService.this;
        }
    }
}
