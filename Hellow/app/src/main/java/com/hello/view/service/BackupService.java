package com.hello.view.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hello.R;
import com.hello.model.db.table.Note;
import com.hello.utils.Log;
import com.hello.utils.rx.Observables;
import com.hello.utils.rx.RxField;
import com.hello.viewmodel.BackupViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.disposables.CompositeDisposable;

import static com.hello.utils.NotificationUtil.getNotification;

public class BackupService extends Service {
    private final int BACKUP_START_ID = 233;
    private final int BACKUP_OVER_ID = 333;
    private final int RESTORE_OVER_ID = 3333;
    private final int RESTORE_START_ID = 2333;

    private CompositeDisposable disposable = new CompositeDisposable();
    private BackupBinder binder;
    private NotificationManager notifyManager;

    @Inject
    BackupViewModel viewModel;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind：BackupService");

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("onUnbind：BackupService");

        stopForeground(true);

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("onCreate：BackupService");

        AndroidInjection.inject(this);

        binder = new BackupBinder();
        notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        viewModel.uploadTalkData();
        addChangeListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy：BackupService");

        viewModel.onCleared();
        disposable.clear();

        super.onDestroy();
    }

    //返回Binder，可以供activity调用
    public class BackupBinder extends Binder {
        public BackupService getService() {
            return BackupService.this;
        }
    }

    public void restoreBackup(List<Note> chooseNotes) {
        if (viewModel.getBackupLoading().get() || viewModel.getRestoreLoading().get()) return;

        viewModel.restoreBackup(chooseNotes);

        notifyManager.notify(RESTORE_START_ID, getNotification(this, false, false,
                R.string.app_name, R.string.text_restore_start, null));
    }

    //外部调用该方法开始执行备份
    public void startBackup() {
        if (viewModel.getBackupLoading().get() || viewModel.getRestoreLoading().get()) return;

        viewModel.startBackup();

        notifyManager.notify(BACKUP_START_ID, getNotification(this, false, false,
                R.string.app_name, R.string.text_backup_start, null));
    }

    private void addChangeListener() {
        RxField.of(viewModel.getBackupLoading())
                .skip(1)
                .filter(b -> !b)
                .compose(Observables.disposable(disposable))
                .doOnNext(__ -> {
                    notifyManager.notify(BACKUP_OVER_ID, getNotification(this, false,
                            false, R.string.app_name, R.string.text_backup_over, null));

                    notifyManager.cancel(BACKUP_START_ID);
                })
                .subscribe();

        RxField.of(viewModel.getRestoreLoading())
                .skip(1)
                .filter(b -> !b)
                .compose(Observables.disposable(disposable))
                .doOnNext(__ -> {
                    notifyManager.notify(RESTORE_OVER_ID, getNotification(this, false,
                            false, R.string.app_name, R.string.text_restore_over, null));

                    notifyManager.cancel(RESTORE_START_ID);
                })
                .subscribe();
    }
}
