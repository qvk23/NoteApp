package com.sun.noteapp.data.source

import android.content.ContentValues
import com.sun.noteapp.data.model.Note

interface NoteDataSource {
    interface Local {
        fun addNote(value: ContentValues, callback: OnDataModifiedCallback<Boolean>)

        fun editNote(id: Int, value: ContentValues, callback: OnDataModifiedCallback<Boolean>)

        fun deleteNote(id: Int, callback: OnDataModifiedCallback<Boolean>)

        fun getAllNotes(callback: OnDataModifiedCallback<List<Note>>)
    }
}
