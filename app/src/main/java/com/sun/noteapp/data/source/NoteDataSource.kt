package com.sun.noteapp.data.source

import com.sun.noteapp.data.model.Note

interface NoteDataSource {
    interface Local {
        fun addNote(note: Note, callback: OnDataModifiedCallback<Boolean>)

        fun editNote(id: Int, note: Note, callback: OnDataModifiedCallback<Boolean>)

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
