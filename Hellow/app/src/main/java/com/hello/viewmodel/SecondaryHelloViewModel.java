package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.hello.common.RxController;
import com.hello.model.aiui.AIUIHolder;
import com.hello.model.data.AppOpenData;
import com.hello.model.data.LightSwitchData;
import com.hello.model.data.MusicData;
import com.hello.model.data.PhoneData;
import com.hello.model.data.PhoneMsgData;
import com.hello.model.data.TuLingData;
import com.hello.utils.Log;
import com.hello.utils.rx.Observables;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class SecondaryHelloViewModel extends RxController {
    public ObservableList<Object> items = new ObservableArrayList<>();
    public ObservableField<MusicData> music = new ObservableField<>();
    public ObservableField<PhoneMsgData> msgData = new ObservableField<>();
    public ObservableField<LightSwitchData> lightData = new ObservableField<>();
    public ObservableField<String> phoneNum = new ObservableField<>();
    public ObservableField<String> location = new ObservableField<>();
    public ObservableField<String> appName = new ObservableField<>();
    public ObservableBoolean musicPlaying = new ObservableBoolean(true);

    public Subject<TuLingData> tuLing = PublishSubject.create();

    private boolean listenAIUI = true;

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    SecondaryHelloViewModel() {
    }

    @Inject
    void init() {
        aiuiHolder.aiuiResult
                .filter(__ -> listenAIUI)
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(v -> {
                    Log.i(v.toString());
                    if (v instanceof PhoneData) {
                        phoneNum.set(null);
                        phoneNum.set(((PhoneData) v).getNumber());
                    } else if (v instanceof PhoneMsgData) {
                        msgData.set(null);
                        msgData.set(((PhoneMsgData) v));
                    } else if (v instanceof LightSwitchData) {
                        lightData.set(null);
                        lightData.set(((LightSwitchData) v));
                    } else if (v instanceof AppOpenData) {
                        appName.set(null);
                        appName.set(((AppOpenData) v).getAppName());
                    } else {
                        items.add(v);
                        if (v instanceof TuLingData) {
                            tuLing.onNext(((TuLingData) v));
                        }
                    }
                })
                .subscribe();

        aiuiHolder.music
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(v -> music.set(v))
                .subscribe();

        aiuiHolder.location
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext(v -> {
                    location.set(null);
                    location.set(v);
                })
                .subscribe();
    }

    public void setListenAIUI(boolean listenAIUI) {
        this.listenAIUI = listenAIUI;
    }
}
