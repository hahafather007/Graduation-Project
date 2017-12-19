package com.hello.viewmodel;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.annimon.stream.Optional;
import com.hello.model.olami.OLAMIHolder;

import javax.inject.Inject;

import ai.olami.cloudService.APIResponse;

public class TodayTodoViewModel {
    public ObservableInt volume = new ObservableInt();
    public ObservableField<APIResponse> apiResponse = new ObservableField<>();

    @Inject
    OLAMIHolder olamiHolder;

    @Inject
    TodayTodoViewModel() {
    }

    @Inject
    void init() {
        olamiHolder.volumeChange
                .map(Optional::get)
                .subscribe(volume::set);

        olamiHolder.voiceCompleted
                .subscribe(__ -> apiResponse.set(olamiHolder.getApiResponse()));
    }

    public void startOrStopRecorder() {
        olamiHolder.startOrStopRecorder();
    }
}
