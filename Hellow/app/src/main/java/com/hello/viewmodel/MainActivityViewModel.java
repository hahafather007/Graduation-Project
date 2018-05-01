package com.hello.viewmodel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.hello.common.RxController;
import com.hello.model.data.UpdateInfoData;
import com.hello.model.service.BackupService;
import com.hello.model.service.UpdateService;
import com.hello.utils.rx.Singles;

import javax.inject.Inject;

public class MainActivityViewModel extends RxController {
    public ObservableField<UpdateInfoData> updateInfo = new ObservableField<>();
    public ObservableBoolean updateLoading = new ObservableBoolean();
    public ObservableBoolean restoreLoading = new ObservableBoolean();

    @Inject
    UpdateService updateService;
    @Inject
    BackupService backupService;

    @Inject
    MainActivityViewModel() {
    }

    @Inject
    void init() {
    }

    public void checkUpdate() {
        if (updateLoading.get()) return;

        updateService.getVersion()
                .compose(Singles.async())
                .compose(Singles.status(updateLoading))
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess(updateInfo::set)
                .subscribe();
    }

    public void restoreNote() {
        if (restoreLoading.get()) return;

        backupService.restoreNote()
                .compose(Singles.async())
                .compose(Singles.status(restoreLoading))
                .compose(Singles.disposable(compositeDisposable))
                .subscribe();
    }
}
