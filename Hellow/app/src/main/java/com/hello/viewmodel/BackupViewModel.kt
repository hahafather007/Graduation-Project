package com.hello.viewmodel

import android.databinding.ObservableBoolean
import com.google.gson.Gson
import com.hello.common.RxController
import com.hello.model.data.NotesBackupAnnalData
import com.hello.model.db.NotesHolder
import com.hello.model.pref.HelloPref
import com.hello.model.service.BackupService
import com.hello.utils.rx.Observables
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class BackupViewModel @Inject constructor() : RxController() {
    val loading = ObservableBoolean()

    @Inject
    lateinit var backupService: BackupService
    @Inject
    lateinit var notesHolder: NotesHolder

    @Inject
    fun init() {
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
                    notesHolder.getNotes().toObservable()
                            .flatMap {
                                Observable.fromIterable(it)
                                        .map { Pair(ids, it) }
                            }
                }
                //没有包含对应的id证明还未备份
                .filter { !it.first.contains(it.second.id) }
                .map { it.second }
                .flatMap { note ->
                    val file = File(note.recordFile)
                    val description = RequestBody.create(
                            MediaType.parse("multipart/form-data"), Gson().toJson(note))
                    val requestFile = RequestBody.create(
                            MediaType.parse("text/plain"), file)
                    val body = MultipartBody.Part.createFormData("music", file.name, requestFile)

                    backupService.backupNote(description, body)
                            .toObservable<Long>()
                            .map { note.id }
                }
                .compose(Observables.async())
                .compose(Observables.status(loading))
                .compose(Observables.disposable(compositeDisposable))
                .doOnNext {
                    val noteIds = Gson().fromJson(HelloPref.noteBackupIds,
                            NotesBackupAnnalData::class.java).noteIds.toMutableList()

                    noteIds.add(it)

                    HelloPref.noteBackupIds = Gson().toJson(NotesBackupAnnalData(noteIds.toList()))
                }
                .subscribe()
    }
}