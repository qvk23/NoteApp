package com.sun.noteapp.data.source

import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.model.NoteOption

interface NoteDataSource {
    interface Local {
        fun addNote(note: Note, callback: OnDataModifiedCallback<Boolean>)

        fun editNote(id: Int, note: Note, callback: OnDataModifiedCallback<Boolean>)

        fun deleteNote(id: Int, callback: OnDataModifiedCallback<Boolean>)

        fun getAllNotes(callback: OnDataModifiedCallback<List<Note>>)

        fun getNotesWithOption(option: NoteOption, callback: OnDataModifiedCallback<List<Note>>)

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
        fun getNoteById(id: Int, callback: OnDataModifiedCallback<Note>)
        fun getNoteCount(callback: OnDataModifiedCallback<Int>)
    }
}
