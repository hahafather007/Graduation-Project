package com.hello.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hello.utils.IntentUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new RxPermissions(this)
                .request(RECEIVE_SMS,
                        READ_CONTACTS,
                        WRITE_CALENDAR,
                        RECORD_AUDIO,
                        READ_PHONE_STATE,
                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION)
                .doOnNext(__ -> {
                    IntentUtil.setupActivity(this, MainActivity.class);
                    finish();
                })
                .subscribe();
    }
}
