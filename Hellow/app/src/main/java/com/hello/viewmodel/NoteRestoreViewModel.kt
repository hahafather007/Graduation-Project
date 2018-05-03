package com.hello.viewmodel

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import com.google.gson.Gson
import com.hello.common.RxController
import com.hello.model.data.NoteListData
import com.hello.model.db.table.Note
import javax.inject.Inject

class NoteRestoreViewModel @Inject constructor() : RxController() {
    val notes = ObservableArrayList<Note>()
    val noteChoosed = ObservableBoolean()

    private val clickedNotes = mutableListOf<Note>()

    fun initNotes(json: String) {
        notes.clear()
        notes.addAll(Gson().fromJson(json, NoteListData::class.java).notes)
    }

    fun toggleClick(note: Note) {
        if (clickedNotes.contains(note)) {
            clickedNotes.remove(note)
        } else {
            clickedNotes.add(note)
        }

        noteChoosed.set(clickedNotes.size > 0)
    }

    fun getClickedNotes() = clickedNotes
}