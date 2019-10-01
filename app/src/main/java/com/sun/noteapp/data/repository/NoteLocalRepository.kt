package com.sun.noteapp.data.repository

import android.content.ContentValues
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.source.NoteDataSource
import com.sun.noteapp.data.source.OnDataModifiedCallback
import com.sun.noteapp.data.source.local.LocalDataSource

class NoteLocalRepository(private val dataSource: LocalDataSource) : NoteDataSource.Local {

    override fun addNote(value: ContentValues, callback: OnDataModifiedCallback<Boolean>) {
        dataSource.addNote(value, callback)
    }

    override fun editNote(
        id: Int,
        value: ContentValues,
        callback: OnDataModifiedCallback<Boolean>
    ) {
        dataSource.editNote(id, value, callback)
    }

    override fun deleteNote(id: Int, callback: OnDataModifiedCallback<Boolean>) {
        dataSource.deleteNote(id, callback)
    }

    override fun getAllNotes(callback: OnDataModifiedCallback<List<Note>>) {
        dataSource.getAllNotes(callback)
    }

    override fun getNotesWithOption(
        color: Int,
        labels: List<String>,
        sortType: String,
        callback: OnDataModifiedCallback<List<Note>>
    ) {
        dataSource.getNotesWithOption(color, labels, sortType, callback)
    }

    override fun getAllLabels(callback: OnDataModifiedCallback<List<String>>) {
        dataSource.getAllLabels(callback)
    }
}
