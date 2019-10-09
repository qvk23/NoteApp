package com.sun.noteapp.data.source

import android.content.ContentValues
import com.sun.noteapp.data.model.Note

interface NoteDataSource {
    interface Local {
        fun addNote(value: ContentValues, callback: OnDataModifiedCallback<Boolean>)

        fun editNote(id: Int, value: ContentValues, callback: OnDataModifiedCallback<Boolean>)

        fun deleteNote(id: Int, callback: OnDataModifiedCallback<Boolean>)

        fun getAllNotes(callback: OnDataModifiedCallback<List<Note>>)

        fun getNotesWithOption(
            color: Int,
            labels: List<String>,
            sortType: String,
            callback: OnDataModifiedCallback<List<Note>>
        )

        fun getAllLabels(callback: OnDataModifiedCallback<List<String>>)

        fun getAllHidedNotes(sortType: String, callback: OnDataModifiedCallback<List<Note>>)

        fun deleteNotes(
            noteIds: List<Int>,
            callback: OnDataModifiedCallback<List<Boolean>>
        )

        fun restoreNotes(
            noteIds: List<Int>,
            callback: OnDataModifiedCallback<List<Boolean>>
        )
    }
}
