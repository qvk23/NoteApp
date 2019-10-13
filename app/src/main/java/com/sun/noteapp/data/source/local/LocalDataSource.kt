package com.sun.noteapp.data.source.local

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.model.NoteOption
import com.sun.noteapp.data.source.NoteDataSource
import com.sun.noteapp.data.source.OnDataModifiedCallback

class LocalDataSource(private val database: NoteDatabase) : NoteDataSource.Local {

    override fun addNote(note: Note, callback: OnDataModifiedCallback<Boolean>) {
        LoadDataAsync(object : LocalDataHandler<Note, Boolean> {
            override fun execute(params: Note): Boolean =
                database.addNote(params)
        }, callback).execute(note)
    }

    override fun editNote(
        id: Int,
        note: Note,
        callback: OnDataModifiedCallback<Boolean>
    ) {
        LoadDataAsync(object : LocalDataHandler<Pair<Int, Note>, Boolean> {
            override fun execute(params: Pair<Int, Note>): Boolean =
                database.updateNote(params.first, params.second)
        }, callback).execute(Pair(id, note))
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
        option: NoteOption,
        callback: OnDataModifiedCallback<List<Note>>
    ) {
        LoadDataAsync(object : LocalDataHandler<NoteOption, List<Note>> {
            override fun execute(params: NoteOption) =
                database.getNotesWithOption(params)
        }, callback).execute(option)
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

    override fun getNoteById(id: Int, callback: OnDataModifiedCallback<Note>) {
        LoadDataAsync(object : LocalDataHandler<Int, Note> {
            override fun execute(params: Int) =
                database.getNoteById(params)
        }, callback).execute(id)
    }

    override fun getNoteCount(callback: OnDataModifiedCallback<Int>) {
        LoadDataAsync(object : LocalDataHandler<String, Int> {
            override fun execute(params: String) =
                database.getNoteCount()
        }, callback).execute("")
    }
}
