package com.sun.noteapp.data.repository

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.source.NoteDataSource
import com.sun.noteapp.data.source.OnDataModifiedCallback
import com.sun.noteapp.data.source.local.LocalDataSource

class NoteLocalRepository(private val dataSource: LocalDataSource) : NoteDataSource.Local {

    override fun addNote(note: Note, callback: OnDataModifiedCallback<Boolean>) {
        dataSource.addNote(note, callback)
    }

    override fun editNote(
        id: Int,
        note: Note,
        callback: OnDataModifiedCallback<Boolean>
    ) {
        dataSource.editNote(id, note, callback)
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

    override fun getAllHidedNotes(sortType: String, callback: OnDataModifiedCallback<List<Note>>) {
        dataSource.getAllHidedNotes(sortType, callback)
    }

    override fun deleteNotes(noteIds: List<Int>, callback: OnDataModifiedCallback<List<Boolean>>) {
        dataSource.deleteNotes(noteIds, callback)
    }

    override fun restoreNotes(noteIds: List<Int>, callback: OnDataModifiedCallback<List<Boolean>>) {
        dataSource.restoreNotes(noteIds, callback)
    }
}
