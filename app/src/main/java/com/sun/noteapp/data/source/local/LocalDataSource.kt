package com.sun.noteapp.data.source.local

import android.content.ContentValues
import android.content.Context
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.source.NoteDataSource
import com.sun.noteapp.data.source.OnDataModifiedCallback

class LocalDataSource(private val database: NoteDatabase) : NoteDataSource.Local {

    override fun addNote(value: ContentValues, callback: OnDataModifiedCallback<Boolean>) {
        LoadDataAsync(object : LocalDataHandler<ContentValues, Boolean> {
            override fun execute(params: ContentValues): Boolean {
                return database.addNote(params)
            }
        }, callback).execute(value)
    }

    override fun editNote(
        id: Int,
        value: ContentValues,
        callback: OnDataModifiedCallback<Boolean>
    ) {
        LoadDataAsync(object : LocalDataHandler<Pair<Int, ContentValues>, Boolean> {
            override fun execute(params: Pair<Int, ContentValues>): Boolean {
                return database.updateNote(params.first, params.second)
            }
        }, callback).execute(Pair(id, value))
    }

    override fun deleteNote(id: Int, callback: OnDataModifiedCallback<Boolean>) {
        LoadDataAsync(object : LocalDataHandler<Int, Boolean> {
            override fun execute(params: Int): Boolean {
                return database.deleteNote(params)
            }
        }, callback).execute(id)
    }

    override fun getAllNotes(callback: OnDataModifiedCallback<List<Note>>) {
        LoadDataAsync(object : LocalDataHandler<String, List<Note>> {
            override fun execute(params: String): List<Note> {
                return database.getAllNote()
            }
        }, callback).execute("")
    }
}
