package com.sun.noteapp.data.model

import android.database.Cursor
import com.sun.noteapp.data.source.local.NoteDatabase

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val type: Int,
    val color: Int,
    val label: String?,
    val date: String?,
    val password: String?
) {
    constructor(cursor: Cursor) : this(
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_ID)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_TITLE)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_CONTENT)),
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_TYPE)),
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_COLOR)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_LABLE)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_DATE)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_PASSWORD))
    )
}
