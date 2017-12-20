package com.hello.viewmodel;

import android.databinding.ObservableField;

import com.hello.model.aiui.AIUIHolder;
import com.hello.model.data.NewsData;

import javax.inject.Inject;

public class TodayToDoViewModel {
    public ObservableField<NewsData> news = new ObservableField<>();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    TodayToDoViewModel() {
    }

    @Inject
    void init() {
/*        aiuiHolder.aiuiResult
                .map(Optional::get)
                .subscribe();*/
    }

    public void startOrStopRecording() {
        aiuiHolder.startOrStopRecording();
    }

    public void sendMessage(String msg) {
        aiuiHolder.sendMessage(msg);
    }
}
