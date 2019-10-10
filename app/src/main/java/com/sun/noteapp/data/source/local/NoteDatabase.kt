package com.sun.noteapp.data.source.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sun.noteapp.data.model.Note
import com.sun.noteapp.data.model.NoteOption
import com.sun.noteapp.utils.TYPE_TEXT_NOTE

class NoteDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun createValue(note: Note) = ContentValues().apply {
        put(NOTE_TITLE, note.title)
        put(NOTE_CONTENT, note.content)
        put(NOTE_TYPE, note.type)
        put(NOTE_LABEL, note.label)
        put(NOTE_COLOR, note.color)
        put(NOTE_MODIFYTIME, note.modifyTime)
        put(NOTE_REMINDTIME, note.remindTime)
        put(NOTE_PASSWORD, note.password)
        put(NOTE_HIDE, note.hide)
    }

    fun addNote(note: Note): Boolean {
        val value = createValue(note)
        val db = writableDatabase
        val result = db.insert(TABLE_NOTE, "", value)
        db.close()
        return result.toInt() != -1
    }

    fun getAllNote(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOTE,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                notes.add(Note(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notes
    }

    fun getAllHidedNote(sortType: String): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOTE,
            null,
            "NOT $NOTE_HIDE = ?",
            arrayOf(UNHIDE),
            null,
            null,
            sortType,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                notes.add(Note(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notes
    }

    fun getAllLabelDataString(): List<String> {
        val result = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOTE,
            arrayOf(NOTE_LABEL),
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(cursor.getColumnIndex(NOTE_LABEL)))
            } while (cursor.moveToNext())
        }
        db.close()
        cursor.close()
        return result
    }

    fun getNotesWithOption(option: NoteOption): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase
        var selection = "$NOTE_HIDE = ? "
        val selectionArgs = ArrayList<String>()
        selectionArgs.add(UNHIDE)

        if (option.color != DEFAULT_COLOR) {
            selection += "AND $NOTE_COLOR = ? "
            selectionArgs.add("${option.color}")
        }

        if (option.labels.isNotEmpty()) {
            option.labels.forEach {
                selection += "AND $NOTE_LABEL LIKE ? "
                selectionArgs.add("%$it%")
            }
        }

        if (option.isOnlyRemind) {
            selection += "AND NOT $NOTE_REMINDTIME = ?"
            selectionArgs.add(Note.NONE)
        }

        val cursor = db.query(
            TABLE_NOTE,
            null,
            selection,
            selectionArgs.toTypedArray(),
            null,
            null,
            option.sortType,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                notes.add(Note(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return notes
    }

    fun updateNote(id: Int, note: Note): Boolean {
        val db = writableDatabase
        val value = createValue(note)
        val selectionArs = arrayOf(id.toString())
        val result = db.update(TABLE_NOTE, value, "$NOTE_ID=?", selectionArs)
        db.close()
        return result != -1
    }

    fun deleteNote(id: Int): Boolean {
        val db = writableDatabase
        val selectionArs = arrayOf(id.toString())
        val result = db.delete(TABLE_NOTE, "$NOTE_ID=?", selectionArs)
        db.close()
        return result != 0
    }

    fun deleteNotes(noteIds: List<Int>): List<Boolean> =
        noteIds.map { deleteNote(it) }

    fun restoreNote(id: Int): Boolean {
        val db = writableDatabase
        val selectionArs = arrayOf(id.toString())
        val value = ContentValues().apply {
            put(NOTE_HIDE, UNHIDE)
        }
        val result = db.update(TABLE_NOTE, value, "$NOTE_ID=?", selectionArs)
        db.close()
        return result != -1
    }

    fun restoreNotes(noteIds: List<Int>): List<Boolean> =
        noteIds.map { restoreNote(it) }

    fun getNoteById(id: Int): Note {
        var note = Note(id, Note.NONE, Note.NONE, TYPE_TEXT_NOTE, DEFAULT_COLOR, Note.NONE, Note.NONE, Note.NONE, Note.NONE, UNHIDE)
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOTE,
            null,
            "$NOTE_ID = ?",
            arrayOf("$id"),
            null,
            null,
            null,
            null
        )
        if (cursor != null && cursor.moveToFirst())
            note = Note(cursor)
        db.close()
        return note!!
    }

    fun getNoteCount(): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NOTE,
            arrayOf(NOTE_ID),
            null,
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToLast()
        val result = cursor.getInt(cursor.getColumnIndex(NOTE_ID))
        cursor.close()
        db.close()
        return result
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Note.db"
        const val TABLE_NOTE = "Note_table"
        const val NOTE_ID = "Note_id"
        const val NOTE_TITLE = "Note_title"
        const val NOTE_CONTENT = "Note_content"
        const val NOTE_LABEL = "Note_label"
        const val NOTE_COLOR = "Note_color"
        const val NOTE_MODIFYTIME = "Note_modify"
        const val NOTE_REMINDTIME = "Note_remind"
        const val NOTE_PASSWORD = "Note_password"
        const val NOTE_TYPE = "Note_type"
        const val NOTE_HIDE = "Note_hide"
        const val ORDERBY_ALPHABETA = "$NOTE_TITLE COLLATE NOCASE ASC"
        const val ORDERBY_COLOR = "$NOTE_COLOR ASC"
        const val ORDERBY_CREATETIME = ""
        const val ORDERBY_MODIFYTIME = "$NOTE_MODIFYTIME DESC"
        const val ORDERBY_REMINDTIME = "$NOTE_REMINDTIME DESC"
        const val TEXT_NOTE = 1
        const val CHECKLIST_NOTE = 2
        const val UNHIDE = "0"
        const val DEFAULT_COLOR = 0

        private const val SQL_CREATE_ENTRIES =
            """create table $TABLE_NOTE ( 
                    $NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                    $NOTE_TITLE TEXT, 
                    $NOTE_CONTENT TEXT, 
                    $NOTE_LABEL TEXT, 
                    $NOTE_TYPE INTEGER, 
                    $NOTE_COLOR INTEGER DEFAULT 0,
                    $NOTE_MODIFYTIME TEXT,
                    $NOTE_REMINDTIME TEXT,  
                    $NOTE_PASSWORD TEXT,
                    $NOTE_HIDE TEXT ) """

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NOTE"
    }
}
