package com.hello.view.service;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;

import com.annimon.stream.Stream;
import com.hello.R;
import com.hello.model.data.AppOpenData;
import com.hello.model.data.LightSwitchData;
import com.hello.model.data.MusicState;
import com.hello.model.data.PhoneData;
import com.hello.model.data.PhoneMsgData;
import com.hello.model.data.SystemMsgData;
import com.hello.model.pref.HelloPref;
import com.hello.receiver.PhoneReceiver;
import com.hello.utils.LightUtil;
import com.hello.utils.Log;
import com.hello.utils.NotificationUtil;
import com.hello.utils.ToastUtil;
import com.hello.utils.rx.Observables;
import com.hello.utils.rx.RxField;
import com.hello.view.activity.MainActivity;
import com.hello.viewmodel.WakeUpViewModel;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDateTime;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;
import static com.hello.common.Constants.ACTION_APP_CREATE;
import static com.hello.common.Constants.ACTION_APP_DESTROY;
import static com.hello.common.Constants.DATA_TIME_FORMAT;
import static com.hello.utils.MusicUtil.MediaListener;
import static com.hello.utils.MusicUtil.playMusic;
import static com.hello.utils.MusicUtil.stopMusic;
import static com.hello.utils.NavigationUtil.openMap;
import static com.hello.utils.NetWorkUtil.isOnline;
import static com.hello.utils.PackageUtil.runAppByName;
import static com.hello.utils.PhonePeopleUtil.getDisplayNameByNumber;

public class WakeUpService extends Service {
    private WakeUpBinder binder;
    private ActivityStateReceiver stateReceiver;
    private PhoneReceiver phoneReceiver;
    //标记activity是否在运行
    private boolean isActivityRunning;
    private CompositeDisposable disposable = new CompositeDisposable();
    //最新接收的短信内容
    private SystemMsgData msgData;

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

        //如果是意外情况启动，就直接结束
        if (!HelloPref.INSTANCE.isCanWakeup()) {
            onDestroy();
        }

        Log.i("onCreate：WakeUpService");

        AndroidInjection.inject(this);

        binder = new WakeUpBinder();

        stateReceiver = new ActivityStateReceiver();
        registerReceiver(stateReceiver, getFilter());

        //监听通话状态，防止占用录音机
        phoneReceiver = new PhoneReceiver() {
            @Override
            public void calling(@NotNull String num) {
                super.calling(num);

                viewModel.stopListening();
            }

            @Override
            public void callingOff() {
                super.callingOff();

                if (!isActivityRunning) {
                    viewModel.startListening();
                }
            }

            @Override
            public void callingOn() {
                super.callingOn();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(phoneReceiver, filter);

        addChangeListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isActivityRunning = intent != null && ACTION_APP_CREATE.equals(intent.getAction());

        if (!isActivityRunning && isOnline(this)) {
            startNotify();
        } else {
            viewModel.stopListening();
        }

        //如果允许后台唤醒才才增加存活度
        if (HelloPref.INSTANCE.isCanWakeup()) {
            return START_STICKY;
        } else {
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy：WakeUpService");

        stopForeground(true);
        unregisterReceiver(stateReceiver);
        unregisterReceiver(phoneReceiver);
        viewModel.onCleared();

        super.onDestroy();
    }

    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_APP_CREATE);
        filter.addAction(ACTION_APP_DESTROY);
        filter.addAction(CONNECTIVITY_ACTION);
        filter.addAction(SMS_RECEIVED_ACTION);

        return filter;
    }

    private void addChangeListener() {
        viewModel.getError()
                .filter(__ -> !isActivityRunning)
                .compose(Observables.disposable(disposable))
                .doOnNext(__ -> ToastUtil.showToast(this, R.string.test_network_error))
                .subscribe();

        RxField.ofNonNull(viewModel.getLocation())
                .filter(__ -> !isActivityRunning)
                .compose(Observables.disposable(disposable))
                .doOnNext(v -> openMap(this, v))
                .subscribe();

        RxField.ofNonNull(viewModel.getMusic())
                .compose(Observables.disposable(disposable))
                .doOnNext(v -> {
                    if (v.getState() == MusicState.ON) {
                        playMusic(v.getUrl(), new MediaListener() {
                            @Override
                            public void error() {
                                ToastUtil.showToast(WakeUpService.this, R.string.test_network_error);
                            }

                            @Override
                            public void complete() {
                                stopMusic();
                            }
                        }, disposable);
                    } else {
                        stopMusic();
                    }
                })
                .subscribe();

        RxField.ofNonNull(viewModel.getResult())
                .filter(__ -> !isActivityRunning)
                .compose(Observables.disposable(disposable))
                .doOnNext(this::checkHowTodo)
                .subscribe();
    }

    private void checkHowTodo(Object obj) {
        if (obj instanceof PhoneData) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + ((PhoneData) obj).getNumber());
            intent.setData(data);
            //检测是否有电话拨打权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ToastUtil.showToast(this, R.string.text_no_phone_permission);

                return;
            }
            startActivity(intent);
        } else if (obj instanceof LightSwitchData) {
            if (((LightSwitchData) obj).getState() == LightSwitchData.State.ON) {
                LightUtil.lightSwitch(this, true);
            } else {
                LightUtil.lightSwitch(this, false);
            }
        } else if (obj instanceof PhoneMsgData) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            Uri data = Uri.parse("smsto:" + ((PhoneMsgData) obj).getNumber());
            intent.setData(data);
            intent.putExtra("sms_body", ((PhoneMsgData) obj).getMsg());
            startActivity(intent);
        } else if (obj instanceof AppOpenData) {
            runAppByName(this, ((AppOpenData) obj).getAppName());
        } else if ((obj instanceof SystemMsgData)) {
            if (msgData != null) {
                String name = getDisplayNameByNumber(this, msgData.getNumber());
                viewModel.speakText(String.format(
                        getString(R.string.text_message_detail), name, msgData.getBody()));
            } else {
                viewModel.speakText(getString(R.string.text_message_no));
            }
        }
    }

    //显示通知栏
    private void startNotify() {
        startForeground(888, NotificationUtil.getNotification(WakeUpService.this,
                false, false, R.string.app_name, R.string.text_hello_stand_by,
                new Intent(WakeUpService.this, MainActivity.class)
                        .addCategory(Intent.CATEGORY_LAUNCHER)
                        .setComponent(new ComponentName(WakeUpService.this, MainActivity.class))
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)));

        viewModel.startListening();
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
                case SMS_RECEIVED_ACTION:
                    if (intent.getExtras() == null) return;
                    //pdus短信单位pdu
                    //解析短信内容
                    Object[] pdus = (Object[]) intent.getExtras().get("pdus");

                    if (pdus == null) break;

                    Stream.of(pdus).forEach(v -> {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) v);

                        String number = sms.getOriginatingAddress();
                        String body = sms.getMessageBody();

                        msgData = new SystemMsgData(number, body,
                                LocalDateTime.now().toString(DATA_TIME_FORMAT));
                    });

                    break;
            }

            //网络变化后检测网络是否可用，减少后台耗电
            if (isOnline(WakeUpService.this) && !isActivityRunning) {
                startNotify();
            } else {
                viewModel.stopListening();
            }

            Log.i("isActivityRunning：" + isActivityRunning);
        }
    }
}
