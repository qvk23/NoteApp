package com.sun.noteapp.ui.textnote

import android.content.ContentValues

interface TextNoteContract {
    interface View {
        fun backToListNote()
    }

    interface Presenter {
        fun addNote(values: ContentValues)
        fun editNote(id: Int, values: ContentValues)
    }
}
