package com.hello.viewmodel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.annimon.stream.Optional;
import com.hello.model.aiui.AIUIHolder;
import com.hello.model.data.NewsData;
import com.hello.utils.Log;

import javax.inject.Inject;

public class TodayToDoViewModel {
    public ObservableField<NewsData> news = new ObservableField<>();
    //消息对话框的内容
    public ObservableList<Object> talkItems = new ObservableArrayList<>();

    @Inject
    AIUIHolder aiuiHolder;

    @Inject
    TodayToDoViewModel() {
    }

    @Inject
    void init() {
/*        aiuiHolder.aiuiResult
                .map(Optional::get)
                .subscribe(v -> Log.i(v.toString()));*/
    }

    public void startOrStopRecording() {
        aiuiHolder.startOrStopRecording();
    }

    public void sendMessage(String msg) {
        aiuiHolder.sendMessage(msg);
    }
}
