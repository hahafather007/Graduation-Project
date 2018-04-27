package com.hello.viewmodel

import android.databinding.ObservableBoolean
import com.google.gson.Gson
import com.hello.common.RxController
import com.hello.model.data.NotesBackupAnnalData
import com.hello.model.db.NotesHolder
import com.hello.model.db.table.Note
import com.hello.model.pref.HelloPref
import com.hello.model.service.BackupService
import com.hello.utils.rx.Completables
import com.hello.utils.rx.Observables
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class BackupViewModel @Inject constructor() : RxController() {
    val backupLoading = ObservableBoolean()
    val restoreLoading = ObservableBoolean()

    @Inject
    lateinit var backupService: BackupService
    @Inject
    lateinit var notesHolder: NotesHolder

    //恢复备份数据,参数为是否恢复录音文件
    fun restoreBackup(fileSave: Boolean) {
        backupService.restoreNote()
                .flatMapObservable { Observable.fromIterable(it) }
                .flatMapSingle { backNote ->
                    notesHolder.getNotes()
                            .map { Pair(backNote, it.map { Pair(it.title, it.time) }) }
                }
                //过滤掉云端与本地重复的数据
                .filter { !it.second.contains(Pair(it.first.title, it.first.time)) }
                .map { it.first }
                .flatMap {
                    Observable.zip(notesHolder.addNote(it.title, it.content, it.recordFile).toObservable(),
                            //如果需要恢复录音数据，则执行恢复录音文件操作
                            if (fileSave) {
                                backupService.restoreFile(it.recordFile).toObservable()
                            } else {
                                Observable.just(it)
                            },
                            BiFunction<Any, Any, Any> { t1, _ -> t1 })
                }
                .compose(Observables.async())
                .compose(Observables.status(restoreLoading))
                .compose(Observables.disposable(compositeDisposable))
                .subscribe()
    }

    //执行备份操作
    fun startBackup() {
        //先获取当前本地所有的note列表，上传到服务器端，与服务器进行同步，再进行备份
        notesHolder.getNotes()
                .flatMapObservable {
                    backupService.synchronizeNote(it)
                            .toObservable<List<Note>>()
                            .map { it }
                }
                .flatMap {
                    Observable.just {
                        if (HelloPref.noteBackupIds != null) {
                            Gson().fromJson(HelloPref.noteBackupIds,
                                    NotesBackupAnnalData::class.java).noteIds
                        } else {
                            emptyList()
                        }
                    }
                            .map { it.invoke() }
                            .flatMap { ids ->
                                Observable.fromIterable(it).map { Pair(ids, it) }
                            }
                }
                //没有包含对应的id证明还未备份
                .filter { !it.first.contains(it.second.id) }
                .map { it.second }
                .flatMapCompletable { note ->
                    val file = File(note.recordFile)
                    val description = RequestBody.create(
                            MediaType.parse("multipart/form-data"), Gson().toJson(note))
                    val requestFile = RequestBody.create(
                            MediaType.parse("text/plain"), file)
                    val body = MultipartBody.Part.createFormData("music", file.name, requestFile)

                    backupService.backupNote(description, body)
                            .doOnComplete {
                                val noteIds = Gson().fromJson(HelloPref.noteBackupIds,
                                        NotesBackupAnnalData::class.java).noteIds.toMutableList()

                                noteIds.add(note.id)

                                HelloPref.noteBackupIds = Gson().toJson(NotesBackupAnnalData(noteIds.toList()))
                            }
                }
                .compose(Completables.async())
                .compose(Completables.status(backupLoading))
                .compose(Completables.disposable(compositeDisposable))
                .subscribe()
    }
}