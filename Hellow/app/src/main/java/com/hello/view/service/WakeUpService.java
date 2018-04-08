package com.hello.view.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hello.R;
import com.hello.model.pref.HelloPref;
import com.hello.utils.Log;
import com.hello.utils.NotificationUtil;
import com.hello.utils.ToastUtil;
import com.hello.utils.rx.Observables;
import com.hello.utils.rx.RxField;
import com.hello.viewmodel.WakeUpViewModel;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.hello.common.Constants.ACTION_APP_CREATE;
import static com.hello.common.Constants.ACTION_APP_DESTROY;
import static com.hello.utils.NavigationUtil.openMap;
import static com.hello.utils.NetWorkUtil.isOnline;

public class WakeUpService extends Service {
    private WakeUpBinder binder;
    private ActivityStateReceiver receiver;
    //标记activity是否在运行
    private boolean isActivityRunning;
    private CompositeDisposable disposable = new CompositeDisposable();

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
        if (intent != null && ACTION_APP_CREATE.equals(intent.getAction())) {
            isActivityRunning = true;
        }

        receiver = new ActivityStateReceiver();
        registerReceiver(receiver, getFilter());

        if (!isActivityRunning && isOnline(this)) {
            startForeground(888, NotificationUtil.getNotification(this,
                    false, false, R.string.app_name, R.string.text_hello_stand_by));

            viewModel.startListening();
        } else {
            viewModel.stopListening();
        }

        addChangeListener();

        //如果允许后台唤醒才才增加存活度
        if (HelloPref.INSTANCE.isCanWakeup()) {
            return START_STICKY;
        } else {
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("onDestroy：WakeUpService");

        stopForeground(true);
        unregisterReceiver(receiver);
        viewModel.onCleared();
    }

    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_APP_CREATE);
        filter.addAction(ACTION_APP_DESTROY);
        filter.addAction(CONNECTIVITY_ACTION);

        return filter;
    }

    private void addChangeListener() {
        viewModel.getError()
                .compose(Observables.disposable(disposable))
                .doOnNext(__ -> ToastUtil.showToast(this, R.string.test_network_error))
                .subscribe();

        RxField.ofNonNull(viewModel.getLocation())
                .compose(Observables.disposable(disposable))
                .doOnNext(v -> openMap(this, v))
                .subscribe();
    }

    //返回Binder，可以供activity调用
    public class WakeUpBinder extends Binder {
        public WakeUpService getService() {
            return WakeUpService.this;
        }
    }

    public class ActivityStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;

            switch (intent.getAction()) {
                case ACTION_APP_CREATE:
                    isActivityRunning = true;

                    stopForeground(true);
                    break;
                case ACTION_APP_DESTROY:
                    isActivityRunning = false;
                    break;
            }

            //网络变化后检测网络是否可用，减少后台耗电
            if (isOnline(WakeUpService.this) && !isActivityRunning) {
                startForeground(888, NotificationUtil.getNotification(WakeUpService.this,
                        false, false, R.string.app_name, R.string.text_hello_stand_by));

                viewModel.startListening();
            } else {
                viewModel.stopListening();
            }

            Log.i("isActivityRunning：" + isActivityRunning);
        }
    }
}
