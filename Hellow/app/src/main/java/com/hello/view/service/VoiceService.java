package com.hello.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hello.R;
import com.hello.utils.Log;
import com.hello.utils.NotificationUtil;
import com.hello.viewmodel.NoteCreateViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class VoiceService extends Service {
    private VoiceBinder binder;

    @Inject
    NoteCreateViewModel viewModel;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind：VoiceService");

        startForeground(666, NotificationUtil.getNotification(this,
                false, false, R.string.app_name, R.string.text_voice_note,null));

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("onUnbind：VoiceService");

        stopForeground(true);

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("onCreate：VoiceService");

        AndroidInjection.inject(this);

        binder = new VoiceBinder();
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy：VoiceService");

        super.onDestroy();
    }

    //返回Binder，可以供activity调用
    public class VoiceBinder extends Binder {
        public VoiceService getService() {
            return VoiceService.this;
        }
    }
}
