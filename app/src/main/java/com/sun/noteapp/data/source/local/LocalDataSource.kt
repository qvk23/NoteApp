package com.sun.noteapp.data.source.local

import android.content.ContentValues
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.source.NoteDataSource
import com.sun.noteapp.data.source.OnDataModifiedCallback

class LocalDataSource(private val database: NoteDatabase) : NoteDataSource.Local {

    override fun addNote(value: ContentValues, callback: OnDataModifiedCallback<Boolean>) {
        LoadDataAsync(object : LocalDataHandler<ContentValues, Boolean> {
            override fun execute(params: ContentValues): Boolean =
                database.addNote(params)
        }, callback).execute(value)
    }

    override fun editNote(
        id: Int,
        value: ContentValues,
        callback: OnDataModifiedCallback<Boolean>
    ) {
        LoadDataAsync(object : LocalDataHandler<Pair<Int, ContentValues>, Boolean> {
            override fun execute(params: Pair<Int, ContentValues>): Boolean =
                database.updateNote(params.first, params.second)
        }, callback).execute(Pair(id, value))
    }

    override fun deleteNote(id: Int, callback: OnDataModifiedCallback<Boolean>) {
        LoadDataAsync(object : LocalDataHandler<Int, Boolean> {
            override fun execute(params: Int): Boolean =
                database.deleteNote(params)
        }, callback).execute(id)
    }

    override fun getAllNotes(callback: OnDataModifiedCallback<List<Note>>) {
        LoadDataAsync(object : LocalDataHandler<String, List<Note>> {
            override fun execute(params: String): List<Note> =
                database.getAllNote()
        }, callback).execute("")
    }

    override fun getNotesWithOption(
        color: Int,
        labels: List<String>,
        sortType: String,
        callback: OnDataModifiedCallback<List<Note>>
    ) {
        LoadDataAsync(object : LocalDataHandler<Triple<Int, List<String>, String>, List<Note>> {
            override fun execute(params: Triple<Int, List<String>, String>): List<Note> =
                database.getNotesWithOption(params.first, params.second, params.third)
        }, callback).execute(Triple(color, labels, sortType))
    }

    override fun getAllLabels(callback: OnDataModifiedCallback<List<String>>) {
        LoadDataAsync(object : LocalDataHandler<String, List<String>> {
            override fun execute(params: String): List<String> =
                database.getAllLabelDataString()
        }, callback).execute("")
    }

    override fun getAllHidedNotes(sortType: String, callback: OnDataModifiedCallback<List<Note>>) {
        LoadDataAsync(object : LocalDataHandler<String, List<Note>> {
            override fun execute(params: String): List<Note> =
                database.getAllHidedNote(params)
        }, callback).execute(sortType)
    }

    override fun deleteNotes(noteIds: List<Int>, callback: OnDataModifiedCallback<List<Boolean>>) {
        LoadDataAsync(object : LocalDataHandler<List<Int>, List<Boolean>> {
            override fun execute(params: List<Int>): List<Boolean> =
                database.deleteNotes(params)
        }, callback).execute(noteIds)
    }

    override fun restoreNotes(noteIds: List<Int>, callback: OnDataModifiedCallback<List<Boolean>>) {
        LoadDataAsync(object : LocalDataHandler<List<Int>, List<Boolean>> {
            override fun execute(params: List<Int>): List<Boolean> =
                database.restoreNotes(params)
        }, callback).execute(noteIds)
    }
}
