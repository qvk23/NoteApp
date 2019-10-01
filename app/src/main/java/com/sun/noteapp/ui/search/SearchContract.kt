package com.sun.noteapp.ui.search

import com.sun.noteapp.data.model.Note

interface SearchContract {
    interface View {
        fun showNotes(notes: List<Note>)
    }

    interface Presenter {
        fun getAllNotes()

        fun searchNote(word: String)
    }
}
