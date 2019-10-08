package com.sun.noteapp.data.model

import android.database.Cursor
import android.os.Parcelable
import com.sun.noteapp.data.source.local.NoteDatabase
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val type: Int,
    val color: Int,
    val label: String,
    val modifyTime: String,
    val remindTime: String,
    val password: String,
    val hide: Int
) : Parcelable {
    constructor(cursor: Cursor) : this(
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_ID)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_TITLE)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_CONTENT)),
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_TYPE)),
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_COLOR)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_LABEL)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_MODIFYTIME)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_REMINDTIME)),
        cursor.getString(cursor.getColumnIndex(NoteDatabase.NOTE_PASSWORD)),
        cursor.getInt(cursor.getColumnIndex(NoteDatabase.NOTE_HIDE))
    )

    companion object {
        const val NONE = "none"
    }
}
