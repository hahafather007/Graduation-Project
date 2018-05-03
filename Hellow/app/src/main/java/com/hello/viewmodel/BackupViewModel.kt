package com.hello.viewmodel

import android.databinding.ObservableBoolean
import com.google.gson.Gson
import com.hello.common.RxController
import com.hello.model.data.NotesBackupAnnalData
import com.hello.model.db.NotesHolder
import com.hello.model.db.SpeakDataHolder
import com.hello.model.db.table.Note
import com.hello.model.pref.HelloPref
import com.hello.model.service.BackupService
import com.hello.model.service.UserTalkService
import com.hello.utils.Log
import com.hello.utils.rx.Completables
import com.hello.utils.rx.Observables
import com.hello.utils.rx.Singles
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class BackupViewModel @Inject constructor() : RxController() {
    val backupLoading = ObservableBoolean()
    val restoreLoading = ObservableBoolean()

    @Inject
    lateinit var backupService: BackupService
    @Inject
    lateinit var notesHolder: NotesHolder
    @Inject
    lateinit var userTalkService: UserTalkService
    @Inject
    lateinit var speakDataHolder: SpeakDataHolder

    @Inject
    fun init() {
        notesHolder.noteEdited
                .flatMapSingle {
                    if (HelloPref.noteBackupIds == null) {
                        Single.just(it)
                    } else {
                        val ids = Gson().fromJson(HelloPref.noteBackupIds,
                                NotesBackupAnnalData::class.java).noteIds.toMutableList()

                        Single.just(it)
                                .map { note ->
                                    if (ids.indexOf(note.id) != -1) {
                                        ids.removeAt(ids.indexOf(note.id))

                                        HelloPref.noteBackupIds = Gson().toJson(NotesBackupAnnalData(ids))
                                    }
                                }
                                .compose(Singles.async())
                    }
                }
                .filter { HelloPref.isAutoBackup }
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext { startBackup() }
                .subscribe()

        notesHolder.noteAdded
                .compose(Observables.disposable(compositeDisposable))
                .filter { HelloPref.isAutoBackup }
                .doOnNext { startBackup() }
                .subscribe()
    }

    //恢复备份数据,参数为是否恢复录音文件
    fun restoreBackup(chooseNotes: List<Note>) {
        Observable.fromIterable(chooseNotes)
                .flatMapCompletable { notesHolder.addNote(it.title, it.content, it.time, it.recordFile) }
                .compose(Completables.async())
                .compose(Completables.status(restoreLoading))
                .compose(Completables.disposable(compositeDisposable))
                .doOnError(Throwable::printStackTrace)
                .subscribe()
    }

    //执行备份操作
    fun startBackup() {
        notesHolder.getNotes()
                .flatMap {
                    Single.just {
                        if (HelloPref.noteBackupIds != null) {
                            Gson().fromJson(HelloPref.noteBackupIds,
                                    NotesBackupAnnalData::class.java).noteIds
                        } else {
                            emptyList()
                        }
                    }
                            .map { it.invoke() }
                            .map { ids -> it.filter { note -> !ids.contains(note.id) } }
                }
                .flatMap {
                    Log.i("note备份数量=${it.size}")

                    backupService.backupNote(it)
                            .toSingleDefault<List<Note>>(it)
                }
                .compose(Singles.async())
                .compose(Singles.status(backupLoading))
                .compose(Singles.disposable(compositeDisposable))
                .doOnSuccess {
                    Log.i("备份成功！！！")

                    if (HelloPref.noteBackupIds != null) {
                        val noteIds = Gson().fromJson(HelloPref.noteBackupIds,
                                NotesBackupAnnalData::class.java).noteIds.toMutableList()

                        it.forEach { note -> noteIds.add(note.id) }

                        HelloPref.noteBackupIds = Gson().toJson(NotesBackupAnnalData(noteIds))
                    } else {
                        val noteIds = mutableListOf<Long>()

                        it.forEach { note -> noteIds.add(note.id) }

                        HelloPref.noteBackupIds = Gson().toJson(NotesBackupAnnalData(noteIds))
                    }

                }
                .doOnError(Throwable::printStackTrace)
                .subscribe()
    }

    //上传用户交互数据
    fun uploadTalkData() {
        speakDataHolder.getSpeakData()
                .flatMap { userTalkService.uploadData(it).toSingleDefault<Any>(it) }
                .flatMapCompletable { speakDataHolder.removeAll() }
                .compose(Completables.async())
                .compose(Completables.disposable(compositeDisposable))
                .doOnError(Throwable::printStackTrace)
                .subscribe()
    }
}