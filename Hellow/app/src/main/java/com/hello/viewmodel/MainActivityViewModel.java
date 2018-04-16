package com.hello.viewmodel;

import android.databinding.ObservableField;

import com.hello.common.RxController;
import com.hello.model.data.UpdateInfoData;
import com.hello.model.service.UpdateService;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

public class MainActivityViewModel extends RxController {
    public ObservableField<UpdateInfoData> updateInfo = new ObservableField<>();

    @Inject
    UpdateService updateService;

    @Inject
    MainActivityViewModel() {
    }

    @Inject
    void init() {
    }

    public void checkUpdate() {
        updateService.getVersion()
                .compose(Singles.async())
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(updateInfo::set)
                .subscribe();
    }
}
